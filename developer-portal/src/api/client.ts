import axios from 'axios'
import { Client, CreateClientRequest, RegenerateApiKeyRequest } from '../types/client'

const api = axios.create({
  baseURL: '/api',
  headers: {
    'Content-Type': 'application/json',
  },
})

export async function registerClient(name: string, password: string): Promise<Client> {
  const request: CreateClientRequest = { name, password }
  const response = await api.post('/clients', request)
  return response.data
}

export async function getClient(clientId: string): Promise<Client> {
  const response = await api.get(`/clients/${clientId}`)
  return response.data
}

export async function regenerateApiKey(
  clientId: string,
  confirmation: string
): Promise<Client> {
  const request: RegenerateApiKeyRequest = { confirmation }
  const response = await api.post(`/clients/${clientId}/regenerate-api-key`, request)
  return response.data
}

export async function deactivateClient(clientId: string): Promise<void> {
  await api.post(`/clients/${clientId}/deactivate`)
}

export async function activateClient(clientId: string): Promise<void> {
  await api.post(`/clients/${clientId}/activate`)
}
