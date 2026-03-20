import type { OrderSummary } from '../types'

type CheckoutStep = 'catalog' | 'order-sheet' | 'payment-method' | 'processing' | 'completed'

interface Product {
  name: string
  price: number
  description: string
}

interface CheckoutPageProps {
  step: CheckoutStep
  products: Product[]
  selectedProduct: Product | null
  draftOrderId: string
  latestOrder?: OrderSummary
  lastResult: { ok: boolean; message: string } | null
  ready: boolean
  orderStatusLabel: (status: OrderSummary['status']) => string
  onMoveToOrderSheet: (product: Product) => void
  onMoveToPaymentMethod: () => void
  onBackToOrderSheet: () => void
  onResetCheckout: () => void
  onRequestCheckout: () => void
  onMoveToOrders: () => void
}

export default function CheckoutPage({
  step,
  products,
  selectedProduct,
  draftOrderId,
  latestOrder,
  lastResult,
  ready,
  orderStatusLabel,
  onMoveToOrderSheet,
  onMoveToPaymentMethod,
  onBackToOrderSheet,
  onResetCheckout,
  onRequestCheckout,
  onMoveToOrders,
}: CheckoutPageProps) {
  return (
    <main className="section checkout-flow">
      <div className="stepper">
        <span className={step === 'catalog' ? 'step active' : 'step'}>1. 상품 선택</span>
        <span className={step === 'order-sheet' ? 'step active' : 'step'}>2. 주문서</span>
        <span className={step === 'payment-method' ? 'step active' : 'step'}>3. 결제 수단</span>
        <span className={step === 'processing' ? 'step active' : 'step'}>4. 결제 확정</span>
        <span className={step === 'completed' ? 'step active' : 'step'}>5. 주문 완료</span>
      </div>

      {step === 'catalog' && (
        <section className="flow-card">
          <h2>상품 선택</h2>
          <p className="flow-sub">구매할 상품을 선택하면 주문서를 작성합니다.</p>
          <div className="product-grid">
            {products.map((product) => (
              <article key={product.name} className="product-card">
                <p className="product-tag">HaruPay</p>
                <h3>{product.name}</h3>
                <p className="product-desc">{product.description}</p>
                <p className="product-price">{product.price.toLocaleString()}원</p>
                <button className="btn-primary" onClick={() => onMoveToOrderSheet(product)}>주문서 작성</button>
              </article>
            ))}
          </div>
        </section>
      )}

      {step === 'order-sheet' && selectedProduct && (
        <section className="flow-card">
          <h2>주문서</h2>
          <p className="flow-sub">주문 정보를 확인한 뒤 결제 수단을 선택합니다.</p>
          <div className="order-sheet">
            <p><span className="label">주문번호</span>{draftOrderId}</p>
            <p><span className="label">상품명</span>{selectedProduct.name}</p>
            <p><span className="label">결제금액</span>{selectedProduct.price.toLocaleString()}원</p>
          </div>
          <div className="flow-actions">
            <button className="btn-stream" onClick={onResetCheckout}>상품 다시 선택</button>
            <button className="btn-primary" onClick={onMoveToPaymentMethod}>결제 수단 선택</button>
          </div>
        </section>
      )}

      {step === 'payment-method' && selectedProduct && (
        <section className="flow-card">
          <h2>결제 수단 선택</h2>
          <p className="flow-sub">현재 지원되는 결제 수단은 하루페이 단독입니다.</p>
          <div className="method-card selected">
            <p className="method-name">하루페이 (HaruPay)</p>
            <p className="method-desc">팝업 결제 후 successUrl에서 결제 확정 및 주문 완료 처리</p>
          </div>
          <div className="flow-actions">
            <button className="btn-stream" onClick={onBackToOrderSheet}>주문서로 돌아가기</button>
            <button className="btn-confirm" onClick={onRequestCheckout} disabled={!ready}>결제하기</button>
          </div>
        </section>
      )}

      {step === 'processing' && (
        <section className="flow-card">
          <h2>결제 확정 처리 중</h2>
          <p className="flow-sub">결제창에서 결제를 완료해 주세요. 완료 후 주문 상태가 자동으로 반영됩니다.</p>
          <div className="processing-box">결제창 완료 대기 / SSE 상태 수신 중...</div>
        </section>
      )}

      {step === 'completed' && (
        <section className="flow-card">
          <h2>주문 완료</h2>
          <p className={lastResult?.ok ? 'result-text success' : 'result-text failure'}>{lastResult?.message ?? '주문 처리 결과를 확인해 주세요.'}</p>
          {latestOrder && (
            <div className="order-sheet">
              <p><span className="label">주문번호</span>{latestOrder.orderId}</p>
              <p><span className="label">결제번호</span><span className="mono">{latestOrder.paymentId}</span></p>
              <p><span className="label">주문상태</span>{orderStatusLabel(latestOrder.status)}</p>
            </div>
          )}
          <div className="flow-actions">
            <button className="btn-stream" onClick={onMoveToOrders}>주문 내역 보기</button>
            <button className="btn-primary" onClick={onResetCheckout}>새 주문 시작</button>
          </div>
        </section>
      )}
    </main>
  )
}
