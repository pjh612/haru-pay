import { useState, useEffect } from 'react'
import { getMyInfo, regenerateMyApiKey } from '../api/client'
import { Client } from '../types/client'
import './MyPage.css'

function MyPage() {
  const [client, setClient] = useState<Client | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [regenerating, setRegenerating] = useState(false)
  const [newApiKey, setNewApiKey] = useState<string | null>(null)
  const [showModal, setShowModal] = useState(false)
  const [copiedClientId, setCopiedClientId] = useState(false)
  const [copiedApiKey, setCopiedApiKey] = useState(false)

  useEffect(() => {
    loadMyInfo()
  }, [])

  const loadMyInfo = async () => {
    try {
      const data = await getMyInfo()
      setClient(data)
    } catch {
      setError('정보를 불러올 수 없습니다.')
    } finally {
      setLoading(false)
    }
  }

  const handleRegenerate = async () => {
    if (!window.confirm('API 키를 재발급하시겠습니까?\n기존 키는 즉시 폐기되며, 새 키는 지금 한 번만 표시됩니다.')) return

    setRegenerating(true)
    try {
      const data = await regenerateMyApiKey()
      setNewApiKey(data.apiKey)
      setShowModal(true)
    } catch {
      setError('API 키 재발급 중 오류가 발생했습니다.')
    } finally {
      setRegenerating(false)
    }
  }

  const handleCloseModal = () => {
    setShowModal(false)
    setNewApiKey(null)
    setCopiedApiKey(false)
  }

  const copyToClipboard = async (text: string, type: 'clientId' | 'apiKey') => {
    await navigator.clipboard.writeText(text)
    if (type === 'clientId') {
      setCopiedClientId(true)
      setTimeout(() => setCopiedClientId(false), 2000)
    } else {
      setCopiedApiKey(true)
      setTimeout(() => setCopiedApiKey(false), 2000)
    }
  }

  if (loading) return <div className="mypage-loading">로딩 중...</div>
  if (error) return <div className="mypage-error">{error}</div>
  if (!client) return null

  return (
    <div className="mypage">
      <h1 className="mypage-title">마이페이지</h1>

      <div className="mypage-card">
        <h2>클라이언트 정보</h2>

        <div className="info-row">
          <label>이름</label>
          <span>{client.name}</span>
        </div>

        <div className="info-row">
          <label>클라이언트 ID</label>
          <div className="copy-row">
            <code className="key-value">{client.id}</code>
            <button
              className="btn-copy"
              onClick={() => copyToClipboard(client.id, 'clientId')}
            >
              {copiedClientId ? '복사됨!' : '복사'}
            </button>
          </div>
        </div>

        <div className="info-row">
          <label>가입일</label>
          <span>{new Date(client.createdAt).toLocaleDateString('ko-KR')}</span>
        </div>

        <div className="info-row">
          <label>상태</label>
          <span className={`status-badge ${client.isActive ? 'active' : 'inactive'}`}>
            {client.isActive ? '활성' : '비활성'}
          </span>
        </div>
      </div>

      <div className="mypage-card">
        <h2>API 키 관리</h2>
        <p className="api-key-notice">
          보안을 위해 API 키는 재발급 시에만 확인할 수 있습니다.
        </p>

        <div className="info-row">
          <label>현재 API 키</label>
          <code className="key-masked">••••••••••••••••••••••••••••••••</code>
        </div>

        <button
          className="btn-regenerate"
          onClick={handleRegenerate}
          disabled={regenerating}
        >
          {regenerating ? '재발급 중...' : 'API 키 재발급'}
        </button>
      </div>

      {showModal && newApiKey && (
        <div className="modal-overlay" onClick={handleCloseModal}>
          <div className="modal" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h3>새 API 키가 발급되었습니다</h3>
            </div>
            <div className="modal-body">
              <div className="modal-warning">
                이 키는 지금 이 화면에서만 확인할 수 있습니다.<br />
                안전한 곳에 저장한 후 창을 닫아주세요.
              </div>
              <div className="modal-key-row">
                <code className="modal-key-value">{newApiKey}</code>
                <button
                  className="btn-copy"
                  onClick={() => copyToClipboard(newApiKey, 'apiKey')}
                >
                  {copiedApiKey ? '복사됨!' : '복사'}
                </button>
              </div>
            </div>
            <div className="modal-footer">
              <button className="btn-close" onClick={handleCloseModal}>
                저장했습니다. 닫기
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}

export default MyPage
