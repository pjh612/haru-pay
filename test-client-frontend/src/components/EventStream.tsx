import { useEventSource } from '../hooks/useEventSource'

interface Props {
  paymentId: string
}

export default function EventStream({ paymentId }: Props) {
  const { events, connected } = useEventSource(paymentId)

  return (
    <div className="section">
      <h2>4. 결제 결과 스트림</h2>
      <p>
        <strong>결제 ID:</strong> {paymentId}
        {connected && <span style={{ color: 'green', marginLeft: 10 }}>연결됨</span>}
      </p>
      <div id="events">
        {events.length === 0 ? (
          <p style={{ color: '#888' }}>이벤트 대기 중...</p>
        ) : (
          events.map((ev, i) => (
            <div key={i} className="event-item" style={ev.data === '연결 오류' ? { color: 'red' } : undefined}>
              {ev.time} - {ev.data}
            </div>
          ))
        )}
      </div>
    </div>
  )
}
