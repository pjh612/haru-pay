import type { OrderSummary } from '../types'

interface OrderHistoryPageProps {
  orders: OrderSummary[]
  orderStatusLabel: (status: OrderSummary['status']) => string
  onRefresh: () => void
}

export default function OrderHistoryPage({ orders, orderStatusLabel, onRefresh }: OrderHistoryPageProps) {
  return (
    <main className="section order-history">
      <div className="section-head">
        <h2>주문 내역</h2>
        <p>결제/주문 상태를 전체 주문 기준으로 확인할 수 있습니다.</p>
      </div>
      <div className="flow-actions history-actions">
        <button className="btn-stream" onClick={onRefresh}>새로고침</button>
      </div>
      {orders.length === 0 ? (
        <p className="empty-text">주문 내역이 없습니다.</p>
      ) : (
        <div className="history-list">
          {orders.map((order) => (
            <article key={order.paymentId} className="history-item">
              <div className="history-head">
                <p className="payment-product">{order.productName}</p>
                <span className={`status-badge status-${order.status}`}>{orderStatusLabel(order.status)}</span>
              </div>
              <p><span className="label">주문번호</span>{order.orderId}</p>
              <p><span className="label">결제번호</span><span className="mono">{order.paymentId}</span></p>
              <p><span className="label">결제금액</span>{Number(order.orderAmount).toLocaleString()}원</p>
              <p><span className="label">주문시각</span>{new Date(order.createdAt).toLocaleString()}</p>
            </article>
          ))}
        </div>
      )}
    </main>
  )
}
