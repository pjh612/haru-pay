import { useEffect, useRef, useState } from 'react'
import type { PaymentStatus, StreamEvent } from '../types'

export function useEventSource(
  paymentId: string | null,
  onStatusChange?: (paymentId: string, status: PaymentStatus) => void,
) {
  const [events, setEvents] = useState<StreamEvent[]>([])
  const [connected, setConnected] = useState(false)
  const esRef = useRef<EventSource | null>(null)

  useEffect(() => {
    if (!paymentId) return

    setEvents([])
    const es = new EventSource('/demo/stream/payments/' + paymentId)
    esRef.current = es
    setConnected(true)

    es.onmessage = (event) => {
      let status: PaymentStatus | undefined
      try {
        const data = JSON.parse(event.data)
        if (data.paymentStatus !== undefined) {
          status = data.paymentStatus === 1 ? 'SUCCEEDED'
            : data.paymentStatus === -1 ? 'FAILED'
            : 'CONFIRMING'
          if (status && onStatusChange) {
            onStatusChange(paymentId, status)
          }
        }
      } catch { /* raw text event */ }

      setEvents(prev => [{
        time: new Date().toLocaleTimeString(),
        data: event.data,
        status,
      }, ...prev])
    }

    es.onerror = () => {
      setEvents(prev => [{
        time: new Date().toLocaleTimeString(),
        data: '연결 오류',
      }, ...prev])
      setConnected(false)
    }

    return () => {
      es.close()
      esRef.current = null
      setConnected(false)
    }
  }, [paymentId, onStatusChange])

  return { events, connected }
}
