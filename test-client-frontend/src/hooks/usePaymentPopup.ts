import { useCallback, useEffect, useRef } from 'react'
import type { PaymentRequestResult } from '../types'

interface Options {
  checkoutUrl: string
  onPaymentComplete: (paymentId: string, result: PaymentRequestResult) => void
}

export function usePaymentPopup({ checkoutUrl, onPaymentComplete }: Options) {
  const popupRef = useRef<Window | null>(null)
  const activePaymentIdRef = useRef<string | null>(null)

  useEffect(() => {
    const handler = (event: MessageEvent) => {
      if (!activePaymentIdRef.current) return

      const data = event.data as PaymentRequestResult
      if (!data?.requestId) return

      popupRef.current?.close()
      popupRef.current = null

      const paymentId = activePaymentIdRef.current
      activePaymentIdRef.current = null
      onPaymentComplete(paymentId, data)
    }

    window.addEventListener('message', handler)
    return () => window.removeEventListener('message', handler)
  }, [onPaymentComplete])

  const openCheckout = useCallback((paymentId: string) => {
    if (popupRef.current && !popupRef.current.closed) {
      popupRef.current.close()
    }

    activePaymentIdRef.current = paymentId
    const url = `${checkoutUrl}/pay/${paymentId}`
    popupRef.current = window.open(
      url,
      'haruPayCheckout',
      'width=500,height=700,scrollbars=no,resizable=no'
    )
  }, [checkoutUrl])

  return { openCheckout }
}
