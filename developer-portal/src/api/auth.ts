import axios from 'axios'
import { LoginRequest, LoginResponse, VerifyEmailRequest } from '../types/auth'

const api = axios.create({
  baseURL: '/api',
  headers: {
    'Content-Type': 'application/json',
  },
  withCredentials: true,
})

export async function login(request: LoginRequest): Promise<LoginResponse> {
  const response = await api.post('/clients/login', request)
  return response.data
}

export async function logout(): Promise<void> {
  await api.post('/clients/logout')
}

export async function verifyEmail(request: VerifyEmailRequest): Promise<void> {
  await api.post('/clients/verify-email', request)
}
