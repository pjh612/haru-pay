import axios from 'axios'
import { Client, CreateClientRequest } from '../types/client'

const api = axios.create({
  baseURL: '/api',
  headers: {
    'Content-Type': 'application/json',
  },
  withCredentials: true,
})

export async function registerClient(email: string, name: string, password: string): Promise<Client> {
  const request: CreateClientRequest = { email, name, password }
  const response = await api.post('/clients', request)
  return response.data
}

export async function verifyEmail(token: string): Promise<void> {
  await api.post('/clients/verify-email', { token })
}

export async function getClient(clientId: string): Promise<Client> {
  const response = await api.get(`/clients/${clientId}`)
  return response.data
}

export async function getMyInfo(): Promise<Client> {
  const response = await api.get('/clients/me')
  return response.data
}

export async function regenerateApiKey(clientId: string): Promise<Client> {
  const response = await api.post(`/clients/${clientId}/regenerate-api-key`)
  return response.data
}

export async function regenerateMyApiKey(): Promise<Client> {
  const response = await api.post('/clients/me/regenerate-api-key')
  return response.data
}

export async function deactivateClient(clientId: string): Promise<void> {
  await api.post(`/clients/${clientId}/deactivate`)
}

export async function activateClient(clientId: string): Promise<void> {
  await api.post(`/clients/${clientId}/activate`)
}
