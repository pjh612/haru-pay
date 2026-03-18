export interface Client {
  id: string
  name: string
  apiKey: string | null
  isActive: boolean
  createdAt: string
}

export interface CreateClientRequest {
  name: string
}

export interface RegenerateApiKeyRequest {
  confirmation: string
}
