import { useEventSource } from '../hooks/useEventSource'
import type { PaymentStatus } from '../types'

interface Props {
  paymentId: string
  onStatusChange: (paymentId: string, status: PaymentStatus) => void
}

export default function EventStream({ paymentId, onStatusChange }: Props) {
  const { events, connected } = useEventSource(paymentId, onStatusChange)

  return (
    <div className="section">
      <h2>4. 결제 결과 스트림</h2>
      <p>
        <strong>결제 ID:</strong> <span className="mono">{paymentId}</span>
        {connected && <span className="connected-badge">연결됨</span>}
      </p>
      <div className="events-container">
        {events.length === 0 ? (
          <p className="empty-text">결과 대기 중...</p>
        ) : (
          events.map((ev, i) => (
            <div key={i} className={`event-item ${ev.data === '연결 오류' ? 'error' : ''}`}>
              {ev.time} - {ev.data}
            </div>
          ))
        )}
      </div>
    </div>
  )
}
