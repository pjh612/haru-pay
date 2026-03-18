import { createContext, useContext, useState, useEffect, ReactNode } from 'react'

interface AuthContextType {
  isAuthenticated: boolean
  clientId: string | null
  clientName: string | null
  login: (clientId: string, clientName: string) => void
  logout: () => void
}

const AuthContext = createContext<AuthContextType | undefined>(undefined)

export function AuthProvider({ children }: { children: ReactNode }) {
  const [isAuthenticated, setIsAuthenticated] = useState(false)
  const [clientId, setClientId] = useState<string | null>(null)
  const [clientName, setClientName] = useState<string | null>(null)

  useEffect(() => {
    const storedClientId = sessionStorage.getItem('clientId')
    const storedClientName = sessionStorage.getItem('clientName')
    if (storedClientId && storedClientName) {
      setClientId(storedClientId)
      setClientName(storedClientName)
      setIsAuthenticated(true)
    }
  }, [])

  const login = (id: string, name: string) => {
    setClientId(id)
    setClientName(name)
    setIsAuthenticated(true)
    sessionStorage.setItem('clientId', id)
    sessionStorage.setItem('clientName', name)
  }

  const logout = () => {
    setClientId(null)
    setClientName(null)
    setIsAuthenticated(false)
    sessionStorage.removeItem('clientId')
    sessionStorage.removeItem('clientName')
  }

  return (
    <AuthContext.Provider value={{ isAuthenticated, clientId, clientName, login, logout }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  const context = useContext(AuthContext)
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider')
  }
  return context
}
