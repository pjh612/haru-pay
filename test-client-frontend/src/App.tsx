import { useCallback, useEffect, useState } from 'react'
import type { AppConfig, MerchantSession, PaymentRequestResult, PaymentStatus, PreparedPayment } from './types'
import { confirmPayment, getConfig, getMerchant, getPayments, preparePayment } from './api/client'
import { usePaymentPopup } from './hooks/usePaymentPopup'
import MerchantRegister from './components/MerchantRegister'
import MerchantInfo from './components/MerchantInfo'
import ProductCatalog from './components/ProductCatalog'
import PaymentList from './components/PaymentList'
import EventStream from './components/EventStream'

export default function App() {
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
    Promise.all([getConfig(), getMerchant()])
      .then(([cfg, m]) => {
        setConfig(cfg)
        setMerchant(m)
        if (m) return refreshPayments()
      })
      .finally(() => setLoading(false))
  }, [refreshPayments])

  const updatePaymentStatus = useCallback((paymentId: string, status: PaymentStatus) => {
    setPayments(prev =>
      prev.map(p => p.paymentId === paymentId ? { ...p, status } : p)
    )
  }, [])

  // 결제창에서 결제 완료 후 postMessage 수신 시
  const handlePaymentComplete = useCallback(async (paymentId: string, _result: PaymentRequestResult) => {
    updatePaymentStatus(paymentId, 'REQUESTED')
  }, [updatePaymentStatus])

  const { openCheckout } = usePaymentPopup({
    checkoutUrl: config?.paymentsCheckoutUrl ?? '',
    onPaymentComplete: handlePaymentComplete,
  })

  // 상품 결제하기 → 가결제 생성 → 결제창 팝업 열기 (원클릭)
  const handleBuy = useCallback(async (productName: string, price: number) => {
    const orderId = `ORDER-${Date.now()}`
    const payment = await preparePayment(orderId, productName, price)
    setPayments(prev => [payment, ...prev])
    openCheckout(payment.paymentId)
  }, [openCheckout])

  // 결제하기 버튼 → 결제창 팝업 열기 (기존 PREPARED 상태에서)
  const handleCheckout = useCallback((paymentId: string) => {
    openCheckout(paymentId)
  }, [openCheckout])

  // 결제 확정 → confirm API 호출 → SSE 구독
  const handleConfirm = useCallback(async (paymentId: string) => {
    try {
      await confirmPayment(paymentId)
      updatePaymentStatus(paymentId, 'CONFIRMING')
      setStreamPaymentId(paymentId)
    } catch (e) {
      alert('확정 실패: ' + (e instanceof Error ? e.message : e))
    }
  }, [updatePaymentStatus])

  // SSE에서 최종 결과 수신 시
  const handleStreamStatus = useCallback((paymentId: string, status: PaymentStatus) => {
    updatePaymentStatus(paymentId, status)
  }, [updatePaymentStatus])

  const handleCleared = () => {
    setMerchant(null)
    setPayments([])
    setStreamPaymentId(null)
  }

  if (loading) return <p>로딩 중...</p>

  return (
    <div>
      <h1>하루페이 (Haru Pay) 테스트 클라이언트</h1>

      <div className="flow-description">
        <p><strong>결제 흐름:</strong> 가맹점 등록 → 상품 선택 (결제하기) → 결제창 팝업 → 결제 확정 → 결과 수신</p>
      </div>

      {!merchant ? (
        <MerchantRegister onRegistered={m => { setMerchant(m); refreshPayments() }} />
      ) : (
        <>
          <MerchantInfo merchant={merchant} onCleared={handleCleared} />
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
        </>
      )}
    </div>
  )
}
