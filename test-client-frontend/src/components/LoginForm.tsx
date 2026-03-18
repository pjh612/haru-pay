import { useState } from 'react'
import type { UserInfo } from '../types'
import { login } from '../api/client'

interface Props {
  onLoggedIn: (user: UserInfo) => void
}

const TEST_ACCOUNTS = [
  { username: 'buyer', password: '1234', label: '구매자' },
  { username: 'admin', password: '1234', label: '관리자' },
]

export default function LoginForm({ onLoggedIn }: Props) {
  const [username, setUsername] = useState('buyer')
  const [password, setPassword] = useState('1234')
  const [error, setError] = useState<string | null>(null)
  const [loading, setLoading] = useState(false)

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError(null)
    setLoading(true)
    try {
      const user = await login(username, password)
      onLoggedIn(user)
    } catch (err) {
      setError(err instanceof Error ? err.message : '로그인 실패')
    } finally {
      setLoading(false)
    }
  }

  const fillAccount = (u: string, p: string) => {
    setUsername(u)
    setPassword(p)
    setError(null)
  }

  return (
    <div className="login-page">
      <div className="login-card">
        <h1 className="login-title">하루페이 (Haru Pay)</h1>
        <p className="login-subtitle">테스트 클라이언트</p>

        {error && <div className="login-error">{error}</div>}

        <form onSubmit={handleSubmit}>
          <div className="login-field">
            <label htmlFor="username">아이디</label>
            <input
              id="username"
              type="text"
              value={username}
              onChange={e => setUsername(e.target.value)}
              autoComplete="username"
            />
          </div>
          <div className="login-field">
            <label htmlFor="password">비밀번호</label>
            <input
              id="password"
              type="password"
              value={password}
              onChange={e => setPassword(e.target.value)}
              autoComplete="current-password"
            />
          </div>
          <button type="submit" className="login-btn" disabled={loading}>
            {loading ? '로그인 중...' : '로그인'}
          </button>
        </form>

        <div className="test-accounts">
          <h3>테스트 계정</h3>
          {TEST_ACCOUNTS.map(acc => (
            <div key={acc.username} className="account-item" onClick={() => fillAccount(acc.username, acc.password)}>
              <span className="account-cred">{acc.username} / {acc.password}</span>
              <span className="account-role">{acc.label}</span>
            </div>
          ))}
        </div>
      </div>
    </div>
  )
}
