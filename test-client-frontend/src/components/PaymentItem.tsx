import type { PreparedPayment } from '../types'

interface Props {
  payment: PreparedPayment
  onConfirm: () => void
  onStream: () => void
}

export default function PaymentItem({ payment, onConfirm, onStream }: Props) {
  const priceFormatted = payment.requestPrice.toLocaleString()
  const { status } = payment

  return (
    <div className="payment-item">
      <div className="payment-info">
        <div className="payment-row">
          <p className="payment-product">{payment.productName}</p>
          <span className={`status-badge status-${status}`}>{statusLabel(status)}</span>
        </div>
        <p className="payment-price">{priceFormatted}원</p>
        <p><span className="label">주문 ID</span> {payment.orderId}</p>
        <p><span className="label">결제 ID</span> <span className="mono">{payment.paymentId}</span></p>
      </div>
      <div className="payment-actions">
        {status === 'PREPARED' && (
          <span className="result-text">결제창에서 결제를 진행해 주세요</span>
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
