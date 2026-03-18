import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { registerClient } from '../api/client'
import './RegisterPage.css'

function RegisterPage() {
  const [name, setName] = useState('')
  const [password, setPassword] = useState('')
  const [confirmPassword, setConfirmPassword] = useState('')
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const navigate = useNavigate()

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    
    if (!name.trim()) {
      setError('클��이언트 이름을 입력해주세요.')
      return
    }

    if (!password.trim()) {
      setError('비밀번호를 입력해주세요.')
      return
    }

    if (password !== confirmPassword) {
      setError('비밀번호가 일치하지 않습니다.')
      return
    }

    if (password.length < 8) {
      setError('비밀번호는 8자 이상이어야 합니다.')
      return
    }

    setLoading(true)
    setError('')

    try {
      const response = await registerClient(name, password)
      navigate('/dashboard/' + response.id, { state: { apiKey: response.apiKey } })
    } catch (err) {
      setError('클��이언트 등록 중 오류가 발생했습니다.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="register-page">
      <div className="register-container">
        <h1>클��이언트 등록</h1>
        <p className="register-description">
          상점 이름을 입력하고 API 키를 발급받으세요.
        </p>

        {error && (
          <div className="error-message">{error}</div>
        )}

        <form onSubmit={handleSubmit} className="register-form">
          <div className="form-group">
            <label htmlFor="name">클��이언트 이름</label>
            <input
              type="text"
              id="name"
              value={name}
              onChange={(e) => setName(e.target.value)}
              placeholder="예: 내 상점"
              disabled={loading}
            />
          </div>

          <div className="form-group">
            <label htmlFor="password">비밀번호</label>
            <input
              type="password"
              id="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="8자 이상의 비밀번호"
              disabled={loading}
            />
          </div>

          <div className="form-group">
            <label htmlFor="confirmPassword">비밀번호 확인</label>
            <input
              type="password"
              id="confirmPassword"
              value={confirmPassword}
              onChange={(e) => setConfirmPassword(e.target.value)}
              placeholder="비밀번호를 다시 입력하세요"
              disabled={loading}
            />
          </div>

          <button
            type="submit"
            className="btn-submit"
            disabled={loading}
          >
            {loading ? '처리 중...' : 'API 키 발급받기'}
          </button>
        </form>

        <div className="register-info">
          <h3>안내사항</h3>
          <ul>
            <li>API 키는 재발급 시에만 확인할 수 있습니다.</li>
            <li>API 키는 안전하게 보관하세요.</li>
            <li>하나의 상점은 하나의 클라이언트를 등록할 수 있습니다.</li>
          </ul>
        </div>
      </div>
    </div>
  )
}

export default RegisterPage
