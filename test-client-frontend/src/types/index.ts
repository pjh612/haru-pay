export interface MerchantSession {
  clientId: string
  clientName: string
  apiKey: string
  registeredAt: string
}

export interface PreparedPayment {
  paymentId: string
  orderId: string
  productName: string
  requestPrice: number
  preparedAt: string
  status: PaymentStatus
}

export type PaymentStatus = 'PREPARED' | 'REQUESTED' | 'CONFIRMING' | 'SUCCEEDED' | 'FAILED'

export interface AppConfig {
  paymentsCheckoutUrl: string
}

export interface PaymentRequestResult {
  requestId: string
  orderId: string
  requestMemberId: string
  productName: string
  requestPrice: number
  clientId: string
  paymentStatus: number
  approvedAt: string | null
}

export interface StreamEvent {
  time: string
  data: string
  status?: string
}
