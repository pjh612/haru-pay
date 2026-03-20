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
  open(options: PrepareCheckoutOptions): Promise<string>
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

    const successUrl = `${window.location.origin}/harupay-success.html`
    const failureUrl = `${window.location.origin}/harupay-failure.html`

    let cancelled = false

    const resolvePrepareUrlWithCsrf = async () => {
      const response = await fetch('/demo/api/csrf', { credentials: 'same-origin' })
      if (!response.ok) {
        throw new Error('CSRF 토큰을 가져오지 못했습니다.')
      }

      const body = await response.json().catch(() => null)
      const token = body?.token
      if (!token) {
        throw new Error('CSRF 토큰이 유효하지 않습니다.')
      }

      const url = new URL(prepareUrl, window.location.origin)
      url.searchParams.set('_csrf', String(token))
      return url.toString()
    }

    const initializeSdk = (securedPrepareUrl: string) => {
      if (cancelled) {
        return
      }

      if (!window.HaruPay) {
        window.setTimeout(() => initializeSdk(securedPrepareUrl), 100)
        return
      }

      sdkRef.current = window.HaruPay.create({
        checkoutUrl,
        prepareUrl: securedPrepareUrl,
        successUrl,
        failureUrl,
      })
      setReady(true)
    }

    const handleScriptLoad = () => {
      resolvePrepareUrlWithCsrf()
        .then((securedPrepareUrl) => initializeSdk(securedPrepareUrl))
        .catch((error) => {
          console.error('HaruPay SDK init failed:', error)
          setReady(false)
        })
    }

    if (window.HaruPay) {
      handleScriptLoad()
      return
    }

    setReady(false)

    const existingScript = document.querySelector<HTMLScriptElement>('script[data-harupay-sdk="true"]')
    if (existingScript) {
      if (existingScript.dataset.loaded === 'true') {
        handleScriptLoad()
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
      handleScriptLoad()
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

      if (data.source === 'harupay-success') {
        if (!data.paymentId) {
          const error = new Error('결제 완료 데이터가 올바르지 않습니다.')
          error.name = 'INVALID_SUCCESS_PAYLOAD'

          if (onPaymentFailure) {
            onPaymentFailure(error)
            return
          }

          alert(`결제창 처리 실패: ${error.message}`)
          return
        }

        onPaymentComplete(data.paymentId, data)
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

  const openPreparedCheckout = useCallback((options: PrepareCheckoutOptions) => {
    return getSdk().open(options)
  }, [])

  return { openPreparedCheckout, ready }
}
