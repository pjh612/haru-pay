import type { PreparedPayment } from '../types'

interface Props {
  payment: PreparedPayment
  onConfirm: () => void
  onStream: () => void
}

export default function PaymentItem({ payment, onConfirm, onStream }: Props) {
  const priceFormatted = payment.requestPrice.toLocaleString()

  return (
    <div className="payment-item">
      <p><strong>결제 ID:</strong> {payment.paymentId}</p>
      <p><strong>주문 ID:</strong> {payment.orderId}</p>
      <p><strong>상품명:</strong> {payment.productName}</p>
      <p><strong>금액:</strong> {priceFormatted}원</p>
      <p>
        <strong>상태:</strong>{' '}
        <span className={`status-badge status-${payment.status}`}>{payment.status}</span>
      </p>
      <button onClick={onStream}>결제 결과 수신</button>{' '}
      <button onClick={onConfirm}>결제 확정</button>
    </div>
  )
}
