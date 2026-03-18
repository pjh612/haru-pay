import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { login } from '../api/auth'
import { useAuth } from '../context/AuthContext'
import './LoginPage.css'

function LoginPage() {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const navigate = useNavigate()
  const { login: authLogin } = useAuth()

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()

    if (!email.trim() || !password.trim()) {
      setError('이메일과 비밀번호를 모두 입력해주세요.')
      return
    }

    setLoading(true)
    setError('')

    try {
      const response = await login({ email, password })
      authLogin(response.id, response.name)
      navigate('/mypage')
    } catch (err: any) {
      if (err.response?.status === 401) {
        setError('이메일 또는 비밀번호가 일치하지 않습니다.')
      } else {
        setError('로그인에 실패했습니다.')
      }
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="login-page">
      <div className="login-container">
        <h1>개발자 로그인</h1>
        <p className="login-description">
          HaruPay 개발자 센터에 로그인하세요.
        </p>

        {error && (
          <div className="error-message">{error}</div>
        )}

        <form onSubmit={handleSubmit} className="login-form">
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
            <label htmlFor="password">비밀번호</label>
            <input
              type="password"
              id="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="비밀번호"
              disabled={loading}
            />
          </div>

          <button
            type="submit"
            className="btn-submit"
            disabled={loading}
          >
            {loading ? '로그인 중...' : '로그인'}
          </button>
        </form>

        <div className="login-footer">
          <p>
            계정이 없으신가요?{' '}
            <Link to="/register" className="link">클라이언트 등록하기</Link>
          </p>
        </div>
      </div>
    </div>
  )
}

export default LoginPage
