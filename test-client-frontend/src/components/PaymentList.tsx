import type { PreparedPayment } from '../types'
import PaymentItem from './PaymentItem'

interface Props {
  payments: PreparedPayment[]
  onCheckout: (paymentId: string) => void
  onConfirm: (paymentId: string) => void
  onStream: (paymentId: string) => void
}

export default function PaymentList({ payments, onCheckout, onConfirm, onStream }: Props) {
  return (
    <div className="section">
      <h2>3. 결제 목록</h2>
      {payments.length === 0 ? (
        <p className="empty-text">생성된 주문이 없습니다.</p>
      ) : (
        payments.map(p => (
          <PaymentItem
            key={p.paymentId}
            payment={p}
            onCheckout={() => onCheckout(p.paymentId)}
            onConfirm={() => onConfirm(p.paymentId)}
            onStream={() => onStream(p.paymentId)}
          />
        ))
      )}
    </div>
  )
}
