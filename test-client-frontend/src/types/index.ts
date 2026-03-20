export interface PreparedPayment {
  paymentId: string
  orderId: string
  productName: string
  requestPrice: number
  preparedAt: string
  status: PaymentStatus
}

export type PaymentStatus = 'PREPARED' | 'REQUESTED' | 'CONFIRMING' | 'SUCCEEDED' | 'FAILED'

export interface PaymentPopupResult {
  paymentId: string
  orderId: string
  requestPrice: number
  paymentStatus?: number | null
  approvedAt?: string | null
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

export interface UserInfo {
  username: string
}

export interface StreamEvent {
  time: string
  data: string
  status?: string
}

export interface OrderSummary {
  orderId: string
  paymentId: string
  productName: string
  orderAmount: number
  status: 'PAYMENT_PREPARED' | 'PAYMENT_CONFIRMING' | 'PAYMENT_COMPLETED' | 'PAYMENT_FAILED'
  createdAt: string
  completedAt: string | null
}
