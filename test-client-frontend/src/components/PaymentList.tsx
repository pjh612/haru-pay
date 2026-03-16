import type { PreparedPayment } from '../types'
import PaymentItem from './PaymentItem'

interface Props {
  payments: PreparedPayment[]
  onConfirm: (paymentId: string) => void
  onStream: (paymentId: string) => void
}

export default function PaymentList({ payments, onConfirm, onStream }: Props) {
  return (
    <div className="section">
      <h2>3. 결제 목록</h2>
      {payments.length === 0 ? (
        <p style={{ color: '#888' }}>준비된 결제가 없습니다.</p>
      ) : (
        payments.map(p => (
          <PaymentItem
            key={p.paymentId}
            payment={p}
            onConfirm={() => onConfirm(p.paymentId)}
            onStream={() => onStream(p.paymentId)}
          />
        ))
      )}
    </div>
  )
}
