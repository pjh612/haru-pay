import { useCallback, useEffect, useState } from 'react'
import type { AppConfig, MerchantSession, PaymentPopupResult, PaymentStatus, PreparedPayment, UserInfo } from './types'
import { confirmPayment, getConfig, getMe, getMerchant, getPayments, logout } from './api/client'
import { usePaymentPopup } from './hooks/usePaymentPopup'
import LoginForm from './components/LoginForm'
import MerchantInfo from './components/MerchantInfo'
import ProductCatalog from './components/ProductCatalog'
import PaymentList from './components/PaymentList'
import EventStream from './components/EventStream'

export default function App() {
  const [user, setUser] = useState<UserInfo | null>(null)
  const [config, setConfig] = useState<AppConfig | null>(null)
  const [merchant, setMerchant] = useState<MerchantSession | null>(null)
  const [payments, setPayments] = useState<PreparedPayment[]>([])
  const [streamPaymentId, setStreamPaymentId] = useState<string | null>(null)
  const [loading, setLoading] = useState(true)

  const refreshPayments = useCallback(async () => {
    const list = await getPayments()
    setPayments(list)
  }, [])

  useEffect(() => {
    Promise.all([getMe(), getConfig()])
      .then(([me, cfg]) => {
        setUser(me)
        setConfig(cfg)
        if (me) {
          return getMerchant().then(m => {
            setMerchant(m)
            if (m) return refreshPayments()
          })
        }
      })
      .finally(() => setLoading(false))
  }, [refreshPayments])

  const loadAfterLogin = useCallback(async (me: UserInfo) => {
    setUser(me)
    const [cfg, m] = await Promise.all([getConfig(), getMerchant()])
    setConfig(cfg)
    setMerchant(m)
    if (m) await refreshPayments()
  }, [refreshPayments])

  const handleLogout = useCallback(async () => {
    await logout()
    setUser(null)
    setMerchant(null)
    setPayments([])
    setStreamPaymentId(null)
  }, [])

  const updatePaymentStatus = useCallback((paymentId: string, status: PaymentStatus) => {
    setPayments(prev =>
      prev.map(p => p.paymentId === paymentId ? { ...p, status } : p)
    )
  }, [])

  const handlePaymentComplete = useCallback(async (paymentId: string, _result: PaymentPopupResult) => {
    updatePaymentStatus(paymentId, 'CONFIRMING')
    setStreamPaymentId(paymentId)
    await refreshPayments()
  }, [refreshPayments, updatePaymentStatus])

  const { openCheckout, openPreparedCheckout, ready } = usePaymentPopup({
    checkoutUrl: config?.paymentsCheckoutUrl ?? '',
    prepareUrl: '/demo/api/payments/prepare',
    onPaymentComplete: handlePaymentComplete,
    onPaymentFailure: (error) => {
      alert('결제창 처리 실패: ' + error.message)
    },
  })

  const handleBuy = useCallback(async (productName: string, price: number) => {
    try {
      const orderId = `ORDER-${Date.now()}`
      await openPreparedCheckout({ orderId, productName, amount: price })
      await refreshPayments()
      setStreamPaymentId(null)
    } catch (e) {
      alert('결제창 열기 실패: ' + (e instanceof Error ? e.message : e))
    }
  }, [openPreparedCheckout, refreshPayments])

  const handleCheckout = useCallback(async (paymentId: string) => {
    try {
      await openCheckout(paymentId)
    } catch (e) {
      alert('결제창 열기 실패: ' + (e instanceof Error ? e.message : e))
    }
  }, [openCheckout])

  const handleConfirm = useCallback(async (paymentId: string) => {
    try {
      await confirmPayment(paymentId)
      updatePaymentStatus(paymentId, 'CONFIRMING')
      setStreamPaymentId(paymentId)
    } catch (e) {
      alert('확정 실패: ' + (e instanceof Error ? e.message : e))
    }
  }, [updatePaymentStatus])

  const handleStreamStatus = useCallback((paymentId: string, status: PaymentStatus) => {
    updatePaymentStatus(paymentId, status)
  }, [updatePaymentStatus])

  if (loading) return <p>로딩 중...</p>

  if (!user) return <LoginForm onLoggedIn={loadAfterLogin} />

  return (
    <div>
      <div className="top-bar">
        <h1>하루페이 (Haru Pay) 테스트 클라이언트</h1>
        <div className="user-info">
          <span className="user-badge">{user.username}</span>
          <button className="logout-btn" onClick={handleLogout}>로그아웃</button>
        </div>
      </div>

      <div className="flow-description">
        <p><strong>결제 흐름:</strong> 로그인 → 상품 선택 (결제하기) → 결제창 팝업 → 결제 확정 → 결과 수신</p>
        <p><strong>SDK 상태:</strong> {ready ? '준비 완료' : '로딩 중...'}</p>
      </div>

      {merchant && <MerchantInfo merchant={merchant} onCleared={() => {}} />}
      <ProductCatalog onBuy={handleBuy} />
      <PaymentList
        payments={payments}
        onCheckout={handleCheckout}
        onConfirm={handleConfirm}
        onStream={setStreamPaymentId}
      />
      {streamPaymentId && (
        <EventStream
          paymentId={streamPaymentId}
          onStatusChange={handleStreamStatus}
        />
      )}
    </div>
  )
}
