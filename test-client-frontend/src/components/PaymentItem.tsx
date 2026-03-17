import type { PreparedPayment } from '../types'

interface Props {
  payment: PreparedPayment
  onCheckout: () => void
  onConfirm: () => void
  onStream: () => void
}

export default function PaymentItem({ payment, onCheckout, onConfirm, onStream }: Props) {
  const priceFormatted = payment.requestPrice.toLocaleString()
  const { status } = payment

  return (
    <div className="payment-item">
      <div className="payment-info">
        <p><strong>결제 ID:</strong> <span className="mono">{payment.paymentId}</span></p>
        <p><strong>주문 ID:</strong> {payment.orderId}</p>
        <p><strong>상품명:</strong> {payment.productName}</p>
        <p><strong>금액:</strong> {priceFormatted}원</p>
        <p>
          <strong>상태:</strong>{' '}
          <span className={`status-badge status-${status}`}>{statusLabel(status)}</span>
        </p>
      </div>
      <div className="payment-actions">
        {status === 'PREPARED' && (
          <button className="btn-primary" onClick={onCheckout}>결제하기</button>
        )}
        {status === 'REQUESTED' && (
          <button className="btn-confirm" onClick={onConfirm}>결제 확정</button>
        )}
        {(status === 'CONFIRMING') && (
          <button className="btn-stream" onClick={onStream}>결과 확인</button>
        )}
        {status === 'SUCCEEDED' && (
          <span className="result-text success">결제 완료</span>
        )}
        {status === 'FAILED' && (
          <span className="result-text failure">결제 실패</span>
        )}
      </div>
    </div>
  )
}

function statusLabel(status: string): string {
  switch (status) {
    case 'PREPARED': return '결제 대기'
    case 'REQUESTED': return '결제 완료 (확정 대기)'
    case 'CONFIRMING': return '확정 처리 중'
    case 'SUCCEEDED': return '성공'
    case 'FAILED': return '실패'
    default: return status
  }
}
