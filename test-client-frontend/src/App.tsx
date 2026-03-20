import { useCallback, useEffect, useMemo, useState } from 'react'
import type { OrderSummary, PaymentPopupResult, UserInfo } from './types'
import { getMe, getOrders, logout } from './api/client'
import { usePaymentPopup } from './hooks/usePaymentPopup'
import LoginForm from './components/LoginForm'
import CheckoutPage from './pages/CheckoutPage'
import OrderHistoryPage from './pages/OrderHistoryPage'

const HARUPAY_CLIENT_ID = 'test-client-frontend'
const HARUPAY_CHECKOUT_URL = 'http://localhost:8071'

type Route = 'checkout' | 'orders'
type CheckoutStep = 'catalog' | 'order-sheet' | 'payment-method' | 'processing' | 'completed'

interface Product {
  name: string
  price: number
  description: string
}

const PRODUCTS: Product[] = [
  { name: '베이직 플랜', price: 9900, description: '기본 구독 플랜' },
  { name: '프로 플랜', price: 29900, description: '프리미엄 구독 플랜' },
  { name: '테스트 상품', price: 1000, description: '테스트용 소액 결제' },
]

function orderStatusLabel(status: OrderSummary['status']) {
  switch (status) {
    case 'PAYMENT_PREPARED': return '결제 준비 완료'
    case 'PAYMENT_CONFIRMING': return '결제 확정 중'
    case 'PAYMENT_COMPLETED': return '주문 완료'
    case 'PAYMENT_FAILED': return '결제 실패'
    default: return status
  }
}

function parseRouteFromHash(hash: string): Route {
  return hash === '#/orders' ? 'orders' : 'checkout'
}

function routeToHash(route: Route): string {
  return route === 'orders' ? '#/orders' : '#/checkout'
}

export default function App() {
  const [user, setUser] = useState<UserInfo | null>(null)
  const [orders, setOrders] = useState<OrderSummary[]>([])
  const [loading, setLoading] = useState(true)
  const [route, setRoute] = useState<Route>(() => parseRouteFromHash(window.location.hash))
  const [step, setStep] = useState<CheckoutStep>('catalog')
  const [selectedProduct, setSelectedProduct] = useState<Product | null>(null)
  const [draftOrderId, setDraftOrderId] = useState('')
  const [lastResult, setLastResult] = useState<{ ok: boolean; message: string } | null>(null)

  const navigate = useCallback((nextRoute: Route) => {
    const nextHash = routeToHash(nextRoute)
    if (window.location.hash !== nextHash) {
      window.history.pushState(null, '', nextHash)
    }
    setRoute(nextRoute)
  }, [])

  useEffect(() => {
    const onHashChange = () => {
      setRoute(parseRouteFromHash(window.location.hash))
    }

    if (!window.location.hash) {
      window.history.replaceState(null, '', '#/checkout')
    }

    window.addEventListener('hashchange', onHashChange)
    return () => window.removeEventListener('hashchange', onHashChange)
  }, [])

  const refreshOrders = useCallback(async () => {
    const list = await getOrders()
    setOrders(list)
    return list
  }, [])

  useEffect(() => {
    getMe()
      .then((me) => {
        setUser(me)
        if (me) {
          return refreshOrders()
        }
      })
      .finally(() => setLoading(false))
  }, [refreshOrders])

  const loadAfterLogin = useCallback(async (me: UserInfo) => {
    setUser(me)
    await refreshOrders()
  }, [refreshOrders])

  const handleLogout = useCallback(async () => {
    await logout()
    setUser(null)
    setOrders([])
    setStep('catalog')
    setSelectedProduct(null)
    setDraftOrderId('')
    setLastResult(null)
    navigate('checkout')
  }, [navigate])

  const moveToOrderSheet = useCallback((product: Product) => {
    const orderId = `${HARUPAY_CLIENT_ID}-ORDER-${Date.now()}`
    setSelectedProduct(product)
    setDraftOrderId(orderId)
    setLastResult(null)
    setStep('order-sheet')
  }, [])

  const moveToPaymentMethod = useCallback(() => {
    if (!selectedProduct || !draftOrderId.trim()) {
      alert('주문 정보를 먼저 확인해 주세요.')
      return
    }
    setStep('payment-method')
  }, [draftOrderId, selectedProduct])

  const resetCheckout = useCallback(() => {
    setStep('catalog')
    setSelectedProduct(null)
    setDraftOrderId('')
    setLastResult(null)
  }, [])

  const pollOrderTerminal = useCallback(async (paymentId: string) => {
    for (let i = 0; i < 12; i++) {
      await new Promise((resolve) => window.setTimeout(resolve, 1500))
      const latest = await refreshOrders()
      const target = latest.find((order) => order.paymentId === paymentId)
      if (!target) continue
      if (target.status === 'PAYMENT_COMPLETED' || target.status === 'PAYMENT_FAILED') {
        return target
      }
    }
    return null
  }, [refreshOrders])

  const handlePaymentComplete = useCallback(async (paymentId: string, result: PaymentPopupResult) => {
    await refreshOrders()

    if (result.paymentStatus === 1) {
      setLastResult({ ok: true, message: '결제가 완료되었습니다. 주문이 확정되었습니다.' })
      setStep('completed')
      return
    }

    if (result.paymentStatus === -1) {
      setLastResult({ ok: false, message: '결제가 실패했습니다. 결제 수단을 다시 확인해 주세요.' })
      setStep('completed')
      return
    }

    setStep('processing')
    const terminal = await pollOrderTerminal(paymentId)
    if (terminal?.status === 'PAYMENT_COMPLETED') {
      setLastResult({ ok: true, message: '결제 확정이 완료되었습니다. 주문이 완료되었습니다.' })
      setStep('completed')
      return
    }

    if (terminal?.status === 'PAYMENT_FAILED') {
      setLastResult({ ok: false, message: '결제 확정에 실패했습니다. 다시 시도해 주세요.' })
      setStep('completed')
      return
    }

    setLastResult({ ok: false, message: '결제 상태를 확인하지 못했습니다. 주문 내역에서 상태를 확인해 주세요.' })
    setStep('completed')
  }, [pollOrderTerminal, refreshOrders])

  const { openPreparedCheckout, ready } = usePaymentPopup({
    checkoutUrl: HARUPAY_CHECKOUT_URL,
    prepareUrl: '/demo/api/payments/prepare',
    onPaymentComplete: handlePaymentComplete,
    onPaymentFailure: (error) => {
      setLastResult({ ok: false, message: `결제창 처리 실패: ${error.message}` })
      setStep('completed')
    },
  })

  const requestCheckout = useCallback(async () => {
    if (!selectedProduct) {
      return
    }

    setLastResult(null)
    setStep('processing')
    try {
      await openPreparedCheckout({
        orderId: draftOrderId,
        productName: selectedProduct.name,
        amount: selectedProduct.price,
      })
      await refreshOrders()
    } catch (e) {
      setLastResult({ ok: false, message: `결제창 열기 실패: ${e instanceof Error ? e.message : e}` })
      setStep('completed')
    }
  }, [draftOrderId, openPreparedCheckout, refreshOrders, selectedProduct])

  const latestOrder = useMemo(() => orders[0], [orders])

  if (loading) return <p>로딩 중...</p>
  if (!user) return <LoginForm onLoggedIn={loadAfterLogin} />

  return (
    <div className="checkout-shell">
      <header className="top-bar">
        <div>
          <p className="eyebrow">HaruPay Checkout</p>
          <h1>주문 / 결제</h1>
        </div>
        <div className="user-info">
          <span className="user-badge">{user.username}</span>
          <button className="logout-btn" onClick={handleLogout}>로그아웃</button>
        </div>
      </header>

      <nav className="tab-nav" role="tablist" aria-label="결제 및 주문 내역 탭">
        <button className={route === 'checkout' ? 'tab active' : 'tab'} onClick={() => navigate('checkout')}>결제 진행</button>
        <button className={route === 'orders' ? 'tab active' : 'tab'} onClick={() => navigate('orders')}>주문 내역</button>
      </nav>

      {route === 'checkout' ? (
        <CheckoutPage
          step={step}
          products={PRODUCTS}
          selectedProduct={selectedProduct}
          draftOrderId={draftOrderId}
          latestOrder={latestOrder}
          lastResult={lastResult}
          ready={ready}
          orderStatusLabel={orderStatusLabel}
          onMoveToOrderSheet={moveToOrderSheet}
          onMoveToPaymentMethod={moveToPaymentMethod}
          onBackToOrderSheet={() => setStep('order-sheet')}
          onResetCheckout={resetCheckout}
          onRequestCheckout={requestCheckout}
          onMoveToOrders={() => navigate('orders')}
        />
      ) : (
        <OrderHistoryPage
          orders={orders}
          orderStatusLabel={orderStatusLabel}
          onRefresh={() => void refreshOrders()}
        />
      )}
    </div>
  )
}
