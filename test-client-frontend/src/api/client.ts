import type { OrderSummary, PreparedPayment, UserInfo } from '../types'

const CSRF_HEADER_NAME = 'X-XSRF-TOKEN'

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
  if (res.status === 401) {
    window.location.href = '/'
    throw new Error('Unauthorized')
  }
  if (!res.ok) throw new Error(`${res.status} ${res.statusText}`)
  return res.json()
}

async function ensureCsrfToken(): Promise<string> {
  const response = await fetch('/demo/api/csrf', { credentials: 'same-origin' })
  if (!response.ok) {
    throw new Error('CSRF 토큰을 가져오지 못했습니다.')
  }

  const body = await response.json().catch(() => null)
  if (body?.token) {
    return String(body.token)
  }

  throw new Error('CSRF 토큰이 유효하지 않습니다.')
}

function withCsrf(headers: HeadersInit | undefined, csrfToken: string): HeadersInit {
  return {
    ...(headers ?? {}),
    [CSRF_HEADER_NAME]: csrfToken,
  }
}

export async function login(username: string, password: string): Promise<UserInfo> {
  const csrfToken = await ensureCsrfToken()
  const res = await fetch('/demo/api/login', {
    method: 'POST',
    headers: withCsrf({ 'Content-Type': 'application/json' }, csrfToken),
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

export async function logout(): Promise<void> {
  const csrfToken = await ensureCsrfToken()
  await fetch('/demo/api/logout', {
    method: 'POST',
    credentials: 'same-origin',
    headers: withCsrf(undefined, csrfToken),
  })
}

export async function preparePayment(orderId: string, productName: string, requestPrice: number, idempotencyKey?: string): Promise<PreparedPayment> {
  const csrfToken = await ensureCsrfToken()
  return request('/demo/api/payments/prepare', {
    method: 'POST',
    headers: withCsrf(withIdempotencyKey({ 'Content-Type': 'application/json' }, idempotencyKey), csrfToken),
    body: JSON.stringify({ orderId, productName, requestPrice }),
  })
}

export async function confirmPayment(paymentId: string, orderId: string, requestPrice: number, idempotencyKey?: string): Promise<void> {
  const csrfToken = await ensureCsrfToken()
  const resolvedIdempotencyKey = idempotencyKey ?? `confirm-${paymentId}`
  return fetch('/demo/api/payments/' + paymentId + '/confirm', {
    method: 'POST',
    headers: withCsrf(withIdempotencyKey({ 'Content-Type': 'application/json' }, resolvedIdempotencyKey), csrfToken),
    credentials: 'same-origin',
    body: JSON.stringify({ orderId, requestPrice }),
  }).then(res => { if (!res.ok) throw new Error('confirm failed') })
}

export function getPayments(): Promise<PreparedPayment[]> {
  return request('/demo/api/payments')
}

export function getOrders(): Promise<OrderSummary[]> {
  return request('/demo/api/orders')
}
