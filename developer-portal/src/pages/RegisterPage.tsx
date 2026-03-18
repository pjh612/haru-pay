import { useState } from 'react'
import { Link } from 'react-router-dom'
import { registerClient } from '../api/client'
import './RegisterPage.css'

function RegisterPage() {
  const [email, setEmail] = useState('')
  const [name, setName] = useState('')
  const [password, setPassword] = useState('')
  const [confirmPassword, setConfirmPassword] = useState('')
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const [registered, setRegistered] = useState(false)

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    
    if (!email.trim()) {
      setError('이메일을 입력해주세요.')
      return
    }

    if (!email.includes('@')) {
      setError('유효한 이메일 주소를 입력해주세요.')
      return
    }

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
      await registerClient(email, name, password)
      setRegistered(true)
    } catch (err: any) {
      if (err.response?.data?.message) {
        setError(err.response.data.message)
      } else {
        setError('클��이언트 등록 중 오류가 발생했습니다.')
      }
    } finally {
      setLoading(false)
    }
  }

  if (registered) {
    return (
      <div className="register-page">
        <div className="register-container">
          <div className="success-message">
            <div className="success-icon">✉️</div>
            <h2>이메일 인증이 필요합니다</h2>
            <p>
              {email}로 인증 이메일을 발송했습니다.
            </p>
            <p>
              이메일을 확인하여 인증을 완료한 후 로그인해주세요.
            </p>
            <div className="success-actions">
              <Link to="/login" className="btn-primary">
                로그인 페이지로 이동
              </Link>
            </div>
          </div>
        </div>
      </div>
    )
  }

  return (
    <div className="register-page">
      <div className="register-container">
        <h1>클��이언트 등록</h1>
        <p className="register-description">
          이메일로 가입하여 API 키를 발급받으세요.
        </p>

        {error && (
          <div className="error-message">{error}</div>
        )}

        <form onSubmit={handleSubmit} className="register-form">
          <div className="form-group">
            <label htmlFor="email">이메일</label>
            <input
              type="email"
              id="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder="example@email.com"
              disabled={loading}
            />
          </div>

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
            {loading ? '처리 중...' : '가입하기'}
          </button>
        </form>

        <div className="register-footer">
          <p>
            이미 계정이 있으신가요?{' '}
            <Link to="/login" className="link">로그인하기</Link>
          </p>
        </div>
      </div>
    </div>
  )
}

export default RegisterPage
