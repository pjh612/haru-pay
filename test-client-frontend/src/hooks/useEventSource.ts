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
        status = toPaymentStatus(data.paymentStatus)
        if (status && onStatusChange) {
          onStatusChange(paymentId, status)
        }

        if (status === 'SUCCEEDED' || status === 'FAILED') {
          es.close()
          esRef.current = null
          setConnected(false)
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

function toPaymentStatus(rawStatus: unknown): PaymentStatus | undefined {
  if (typeof rawStatus !== 'number') {
    return undefined
  }

  if (rawStatus === 1) {
    return 'SUCCEEDED'
  }

  if (rawStatus === -1) {
    return 'FAILED'
  }

  return 'CONFIRMING'
}
