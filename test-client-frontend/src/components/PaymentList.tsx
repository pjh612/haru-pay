import type { PreparedPayment } from '../types'
import PaymentItem from './PaymentItem'

interface Props {
  payments: PreparedPayment[]
  onConfirm: (paymentId: string) => void
  onStream: (paymentId: string) => void
}

export default function PaymentList({ payments, onConfirm, onStream }: Props) {
  return (
    <div className="section payment-section">
      <div className="section-head">
        <h2>주문 결제 내역</h2>
        <p>요청된 결제를 확정하고 실시간 상태를 확인합니다.</p>
      </div>
      {payments.length === 0 ? (
        <p className="empty-text">생성된 주문이 없습니다.</p>
      ) : (
        <div className="payment-list">
          {payments.map(p => (
            <PaymentItem
              key={p.paymentId}
              payment={p}
              onConfirm={() => onConfirm(p.paymentId)}
              onStream={() => onStream(p.paymentId)}
            />
          ))}
        </div>
      )}
    </div>
  )
}
