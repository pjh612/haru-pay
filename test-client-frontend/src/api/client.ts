import type { AppConfig, MerchantSession, PreparedPayment } from '../types'

async function request<T>(url: string, options?: RequestInit): Promise<T> {
  const res = await fetch(url, { credentials: 'same-origin', ...options })
  if (!res.ok) throw new Error(`${res.status} ${res.statusText}`)
  return res.json()
}

export function getConfig(): Promise<AppConfig> {
  return request('/demo/api/config')
}

export function getMerchant(): Promise<MerchantSession | null> {
  return fetch('/demo/api/merchant', { credentials: 'same-origin' })
    .then(res => res.status === 204 ? null : res.json())
}

export function registerMerchant(name: string): Promise<MerchantSession> {
  return request('/demo/api/register?name=' + encodeURIComponent(name), { method: 'POST' })
}

export function preparePayment(orderId: string, productName: string, requestPrice: number): Promise<PreparedPayment> {
  const params = new URLSearchParams({ orderId, productName, requestPrice: String(requestPrice) })
  return request('/demo/api/payments/prepare?' + params, { method: 'POST' })
}

export function confirmPayment(paymentId: string): Promise<void> {
  return fetch('/demo/api/payments/' + paymentId + '/confirm', {
    method: 'POST',
    credentials: 'same-origin',
  }).then(res => { if (!res.ok) throw new Error('confirm failed') })
}

export function getPayments(): Promise<PreparedPayment[]> {
  return fetch('/demo/api/payments', { credentials: 'same-origin' })
    .then(res => res.ok ? res.json() : [])
}

export function clearSession(): Promise<void> {
  return fetch('/demo/api/session', { method: 'DELETE', credentials: 'same-origin' })
    .then(() => {})
}
