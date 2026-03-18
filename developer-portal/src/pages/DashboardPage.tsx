import { useState, useEffect } from 'react'
import { useParams, useLocation } from 'react-router-dom'
import { getClient, regenerateApiKey, deactivateClient, activateClient } from '../api/client'
import { Client } from '../types/client'
import './DashboardPage.css'

function DashboardPage() {
  const { clientId } = useParams<{ clientId: string }>()
  const location = useLocation()
  const [client, setClient] = useState<Client | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [showApiKey, setShowApiKey] = useState(false)
  const [regenerating, setRegenerating] = useState(false)
  
  const initialApiKey = location.state?.apiKey

  useEffect(() => {
    loadClient()
  }, [clientId])

  const loadClient = async () => {
    if (!clientId) return
    
    try {
      const data = await getClient(clientId)
      setClient(data)
    } catch (err) {
      setError('클라이언트 정보를 불러올 수 없습니다.')
    } finally {
      setLoading(false)
    }
  }

  const handleRegenerate = async () => {
    if (!clientId) return
    
    const confirmed = window.confirm(
      'API 키를 재발급하시겠습니까? 기존 키는 즉시 폐기됩니다.'
    )
    
    if (!confirmed) return

    setRegenerating(true)
    try {
      const data = await regenerateApiKey(clientId)
      setClient(data)
      setShowApiKey(true)
    } catch (err) {
      setError('API 키 재발급 중 오류가 발생했습니다.')
    } finally {
      setRegenerating(false)
    }
  }

  const handleToggleStatus = async () => {
    if (!clientId || !client) return

    try {
      if (client.isActive) {
        await deactivateClient(clientId)
      } else {
        await activateClient(clientId)
      }
      loadClient()
    } catch (err) {
      setError('상태 변경 중 오류가 발생했습니다.')
    }
  }

  if (loading) return <div className="loading">로딩 중...</div>
  if (error) return <div className="error">{error}</div>
  if (!client) return <div className="error">클라이언트를 찾을 수 없습니다.</div>

  return (
    <div className="dashboard">
      <div className="dashboard-header">
        <h1>{client.name}</h1>
        <div className={`status-badge ${client.isActive ? 'active' : 'inactive'}`}>
          {client.isActive ? '활성' : '비활성'}
        </div>
      </div>

      {initialApiKey && (
        <div className="api-key-banner">
          <div className="banner-header">
            <span className="banner-icon">⚠️</span>
            <span>API 키가 발급되었습니다. 안전하게 저장하세요!</span>
          </div>
          <code className="api-key-code">{initialApiKey}</code>
          <p className="banner-warning">
            이 키는 다시 표시되지 않습니다. 분실 시 재발급이 필요합니다.
          </p>
        </div>
      )}

      <div className="dashboard-grid">
        <div className="dashboard-card">
          <h2>API 키 관리</h2>
          
          <div className="api-key-section">
            <label>현재 API 키</label>
            <div className="api-key-display">
              {showApiKey ? (
                <code>{client.apiKey || '***'}</code>
              ) : (
                <code>********************</code>
              )}
              <button
                className="btn-toggle"
                onClick={() => setShowApiKey(!showApiKey)}
              >
                {showApiKey ? '숨기기' : '보기'}
              </button>
            </div>
          </div>

          <button
            className="btn-regenerate"
            onClick={handleRegenerate}
            disabled={regenerating}
          >
            {regenerating ? '처리 중...' : 'API 키 재발급'}
          </button>
        </div>

        <div className="dashboard-card">
          <h2>클라이언트 정보</h2>
          
          <div className="info-row">
            <label>클라이언트 ID</label>
            <code>{client.id}</code>
          </div>
          
          <div className="info-row">
            <label>등록일</label>
            <span>{new Date(client.createdAt).toLocaleDateString()}</span>
          </div>

          <div className="info-row">
            <label>상태</label>
            <button
              className={`btn-status ${client.isActive ? 'active' : 'inactive'}`}
              onClick={handleToggleStatus}
            >
              {client.isActive ? '비활성화' : '활성화'}
            </button>
          </div>
        </div>
      </div>

      <div className="quick-links">
        <h2>빠른 링크</h2>
        <div className="link-grid">
          <a href="/docs" className="quick-link">
            <span>📚</span>
            API 문서
          </a>
          <a href="#" className="quick-link">
            <span>💳</span>
            결제 테스트
          </a>
        </div>
      </div>
    </div>
  )
}

export default DashboardPage
