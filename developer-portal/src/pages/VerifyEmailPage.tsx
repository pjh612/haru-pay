import { useEffect, useState } from 'react'
import { useSearchParams, useNavigate } from 'react-router-dom'
import { verifyEmail } from '../api/auth'

export default function VerifyEmailPage() {
  const [searchParams] = useSearchParams()
  const navigate = useNavigate()
  const [status, setStatus] = useState<'loading' | 'success' | 'error'>('loading')
  const [message, setMessage] = useState('이메일 인증을 처리 중입니다...')

  useEffect(() => {
    const token = searchParams.get('token')

    if (!token) {
      setStatus('error')
      setMessage('인증 토큰이 없습니다. 올바른 링크로 접근해주세요.')
      return
    }

    const verify = async () => {
      try {
        await verifyEmail({ token })
        setStatus('success')
        setMessage('이메일 인증이 완료되었습니다! 이제 로그인하실 수 있습니다.')

        setTimeout(() => {
          navigate('/login')
        }, 3000)
      } catch (error) {
        setStatus('error')
        setMessage('이메일 인증에 실패했습니다. 토큰이 유효하지 않거나 만료되었을 수 있습니다.')
      }
    }

    verify()
  }, [searchParams, navigate])

  const getStatusIcon = () => {
    const iconWrapBase = 'mx-auto w-16 h-16 rounded-2xl flex items-center justify-center mb-6'

    switch (status) {
      case 'loading':
        return (
          <div className={`${iconWrapBase}`} style={{ background: 'linear-gradient(135deg, rgba(59,130,246,0.15) 0%, rgba(59,130,246,0.05) 100%)', border: '1px solid rgba(59,130,246,0.12)' }}>
            <svg className="animate-spin h-8 w-8" style={{ color: '#60a5fa' }} xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
              <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
              <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
            </svg>
          </div>
        )
      case 'success':
        return (
          <div className={`${iconWrapBase}`} style={{ background: 'linear-gradient(135deg, rgba(34,197,94,0.15) 0%, rgba(34,197,94,0.05) 100%)', border: '1px solid rgba(34,197,94,0.12)' }}>
            <svg className="h-8 w-8" style={{ color: '#4ade80' }} fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2.5} d="M5 13l4 4L19 7" />
            </svg>
          </div>
        )
      case 'error':
        return (
          <div className={`${iconWrapBase}`} style={{ background: 'linear-gradient(135deg, rgba(239,68,68,0.15) 0%, rgba(239,68,68,0.05) 100%)', border: '1px solid rgba(239,68,68,0.12)' }}>
            <svg className="h-8 w-8" style={{ color: '#f87171' }} fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
            </svg>
          </div>
        )
    }
  }

  const titleColor = status === 'loading' ? '#60a5fa' : status === 'success' ? '#4ade80' : '#f87171'

  return (
    <div className="flex justify-center items-center" style={{ minHeight: '60vh', padding: '2rem' }}>
      <div
        className="w-full text-center"
        style={{
          maxWidth: 440,
          background: 'rgba(24, 24, 28, 0.7)',
          backdropFilter: 'blur(20px)',
          WebkitBackdropFilter: 'blur(20px)',
          border: '1px solid rgba(255,255,255,0.06)',
          borderRadius: 20,
          padding: '2.5rem',
        }}
      >
        {getStatusIcon()}

        <h1
          className="text-xl font-bold mb-2"
          style={{ color: titleColor, letterSpacing: '-0.02em' }}
        >
          {status === 'loading' && '이메일 인증'}
          {status === 'success' && '인증 완료!'}
          {status === 'error' && '인증 실패'}
        </h1>

        <p className="mb-8" style={{ color: '#71717a', fontSize: '0.9375rem', lineHeight: 1.7 }}>
          {message}
        </p>

        {status === 'success' && (
          <button
            onClick={() => navigate('/login')}
            className="w-full font-semibold"
            style={{
              background: 'linear-gradient(135deg, #3b82f6 0%, #2563eb 100%)',
              color: '#fff',
              padding: '0.875rem',
              borderRadius: 12,
              border: 'none',
              fontSize: '0.9375rem',
              cursor: 'pointer',
              boxShadow: '0 4px 20px rgba(59,130,246,0.25), inset 0 1px 0 rgba(255,255,255,0.1)',
            }}
          >
            로그인 페이지로 이동
          </button>
        )}

        {status === 'error' && (
          <div className="space-y-3">
            <button
              onClick={() => navigate('/register')}
              className="w-full font-semibold"
              style={{
                background: 'linear-gradient(135deg, #3b82f6 0%, #2563eb 100%)',
                color: '#fff',
                padding: '0.875rem',
                borderRadius: 12,
                border: 'none',
                fontSize: '0.9375rem',
                cursor: 'pointer',
                boxShadow: '0 4px 20px rgba(59,130,246,0.25), inset 0 1px 0 rgba(255,255,255,0.1)',
              }}
            >
              다시 가입하기
            </button>
            <button
              onClick={() => navigate('/login')}
              className="w-full font-medium"
              style={{
                background: 'rgba(255,255,255,0.06)',
                color: '#a1a1aa',
                padding: '0.875rem',
                borderRadius: 12,
                border: '1px solid rgba(255,255,255,0.08)',
                fontSize: '0.9375rem',
                cursor: 'pointer',
              }}
            >
              로그인 페이지로
            </button>
          </div>
        )}
      </div>
    </div>
  )
}
