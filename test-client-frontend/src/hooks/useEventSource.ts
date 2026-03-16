import { useEffect, useRef, useState } from 'react'
import type { StreamEvent } from '../types'

export function useEventSource(paymentId: string | null) {
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
      let status: string | undefined
      try {
        const data = JSON.parse(event.data)
        if (data.paymentStatus !== undefined) {
          status = data.paymentStatus === 2 ? 'SUCCEEDED'
            : data.paymentStatus === 3 ? 'FAILED'
            : 'CONFIRMING'
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
  }, [paymentId])

  return { events, connected }
}
