export interface LoginRequest {
  email: string
  password: string
}

export interface LoginResponse {
  id: string
  name: string
  isActive: boolean
  createdAt: string
  sessionId: string
}
