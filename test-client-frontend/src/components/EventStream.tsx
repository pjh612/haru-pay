import { useEventSource } from '../hooks/useEventSource'
import type { PaymentStatus } from '../types'

interface Props {
  paymentId: string
  onStatusChange: (paymentId: string, status: PaymentStatus) => void
}

export default function EventStream({ paymentId, onStatusChange }: Props) {
  const { events, connected } = useEventSource(paymentId, onStatusChange)

  return (
    <div className="section stream-section">
      <div className="section-head">
        <h2>실시간 결제 상태</h2>
        <p>결제 확정 후 승인 이벤트를 SSE로 수신합니다.</p>
      </div>
      <p className="stream-meta">
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
