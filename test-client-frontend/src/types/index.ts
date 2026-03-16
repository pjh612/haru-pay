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
  status: 'PREPARED' | 'CONFIRMING' | 'SUCCEEDED' | 'FAILED'
}

export interface StreamEvent {
  time: string
  data: string
  status?: string
}
