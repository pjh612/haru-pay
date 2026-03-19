import type { AppConfig, MerchantSession, PreparedPayment, UserInfo } from '../types'

function withIdempotencyKey(headers: HeadersInit | undefined, idempotencyKey?: string): HeadersInit | undefined {
  if (!idempotencyKey) {
    return headers
  }

  return {
    ...(headers ?? {}),
    'Idempotency-Key': idempotencyKey,
  }
}

async function request<T>(url: string, options?: RequestInit): Promise<T> {
  const res = await fetch(url, { credentials: 'same-origin', ...options })
  if (!res.ok) throw new Error(`${res.status} ${res.statusText}`)
  return res.json()
}

export async function login(username: string, password: string): Promise<UserInfo> {
  const res = await fetch('/demo/api/login', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    credentials: 'same-origin',
    body: JSON.stringify({ username, password }),
  })
  if (!res.ok) {
    const data = await res.json().catch(() => null)
    throw new Error(data?.error ?? '로그인 실패')
  }
  return res.json()
}

export function getMe(): Promise<UserInfo | null> {
  return fetch('/demo/api/me', { credentials: 'same-origin' })
    .then(res => res.ok ? res.json() : null)
}

export function logout(): Promise<void> {
  return fetch('/demo/api/logout', { method: 'POST', credentials: 'same-origin' })
    .then(() => {})
}

export function getConfig(): Promise<AppConfig> {
  return request('/demo/api/config')
}

export function getMerchant(): Promise<MerchantSession | null> {
  return fetch('/demo/api/merchant', { credentials: 'same-origin' })
    .then(res => res.status === 204 ? null : res.json())
}

export function preparePayment(orderId: string, productName: string, requestPrice: number, idempotencyKey?: string): Promise<PreparedPayment> {
  return request('/demo/api/payments/prepare', {
    method: 'POST',
    headers: withIdempotencyKey({ 'Content-Type': 'application/json' }, idempotencyKey),
    body: JSON.stringify({ orderId, productName, requestPrice }),
  })
}

export function confirmPayment(paymentId: string, idempotencyKey?: string): Promise<void> {
  return fetch('/demo/api/payments/' + paymentId + '/confirm', {
    method: 'POST',
    headers: withIdempotencyKey(undefined, idempotencyKey),
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
