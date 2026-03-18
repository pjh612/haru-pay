import { useCallback, useEffect, useRef, useState } from 'react'
import type { PaymentPopupResult } from '../types'

interface Options {
  checkoutUrl: string
  prepareUrl: string
  onPaymentComplete: (paymentId: string, result: PaymentPopupResult) => void
  onPaymentFailure?: (error: Error) => void
}

interface PrepareCheckoutOptions {
  orderId: string
  productName: string
  amount: number
}

interface HaruPayConfig {
  checkoutUrl: string
  prepareUrl: string
  successUrl: string
  failureUrl: string
}

interface HaruPayInstance {
  open(options: { paymentId: string } | PrepareCheckoutOptions): Promise<string>
}

declare global {
  interface Window {
    HaruPay?: {
      create(config: HaruPayConfig): HaruPayInstance
    }
  }
}

export function usePaymentPopup({ checkoutUrl, prepareUrl, onPaymentComplete, onPaymentFailure }: Options) {
  const sdkRef = useRef<HaruPayInstance | null>(null)
  const [ready, setReady] = useState(false)

  useEffect(() => {
    if (!checkoutUrl) {
      sdkRef.current = null
      setReady(false)
      return
    }

    const successUrl = `${window.location.origin}/demo/payments/success`
    const failureUrl = `${window.location.origin}/demo/payments/failure`

    let cancelled = false

    const initializeSdk = (remainingAttempts = 10) => {
      if (cancelled) {
        return
      }

      if (!window.HaruPay) {
        if (remainingAttempts > 0) {
          window.setTimeout(() => initializeSdk(remainingAttempts - 1), 100)
        }
        return
      }

      sdkRef.current = window.HaruPay.create({
        checkoutUrl,
        prepareUrl,
        successUrl,
        failureUrl,
      })
      setReady(true)
    }

    const handleScriptLoad = () => {
      initializeSdk()
    }

    if (window.HaruPay) {
      initializeSdk()
      return
    }

    setReady(false)

    const existingScript = document.querySelector<HTMLScriptElement>('script[data-harupay-sdk="true"]')
    if (existingScript) {
      if (existingScript.dataset.loaded === 'true') {
        initializeSdk()
        return () => {
          cancelled = true
        }
      }

      const handleScriptError = () => {
        console.error('HaruPay SDK script load failed:', existingScript.src)
        setReady(false)
      }

      existingScript.addEventListener('load', handleScriptLoad, { once: true })
      existingScript.addEventListener('error', handleScriptError, { once: true })
      return () => {
        cancelled = true
        existingScript.removeEventListener('load', handleScriptLoad)
        existingScript.removeEventListener('error', handleScriptError)
      }
    }

    const script = document.createElement('script')
    script.src = `${checkoutUrl}/js/harupay.js`
    script.async = false
    script.dataset.harupaySdk = 'true'
    script.addEventListener('load', () => {
      script.dataset.loaded = 'true'
      initializeSdk()
    }, { once: true })
    script.addEventListener('error', () => {
      console.error('HaruPay SDK script load failed:', script.src)
      setReady(false)
    }, { once: true })
    document.body.appendChild(script)

    return () => {
      cancelled = true
    }
  }, [checkoutUrl, onPaymentComplete, onPaymentFailure, prepareUrl])

  useEffect(() => {
    const handler = (event: MessageEvent) => {
      if (event.origin !== window.location.origin) return

      const data = event.data as ({ source?: string, errorCode?: string, message?: string } & PaymentPopupResult)
      if (!data?.source) return

      if (data.source === 'harupay-success' && data.requestId) {
        onPaymentComplete(data.requestId, data)
        return
      }

      if (data.source === 'harupay-failure') {
        const error = new Error(data.message || '결제창 처리에 실패했습니다.')
        error.name = data.errorCode || 'PAYMENT_FAILED'

        if (onPaymentFailure) {
          onPaymentFailure(error)
          return
        }

        alert(`결제창 처리 실패: ${error.message}`)
      }
    }

    window.addEventListener('message', handler)
    return () => window.removeEventListener('message', handler)
  }, [onPaymentComplete, onPaymentFailure])

  const getSdk = () => {
    if (!sdkRef.current) {
      throw new Error('HaruPay SDK가 아직 준비되지 않았습니다.')
    }

    return sdkRef.current
  }

  const openCheckout = useCallback((paymentId: string) => {
    return getSdk().open({ paymentId })
  }, [])

  const openPreparedCheckout = useCallback((options: PrepareCheckoutOptions) => {
    return getSdk().open(options)
  }, [])

  return { openCheckout, openPreparedCheckout, ready }
}
