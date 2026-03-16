import { useState } from 'react'
import { registerMerchant } from '../api/client'
import type { MerchantSession } from '../types'

interface Props {
  onRegistered: (merchant: MerchantSession) => void
}

export default function MerchantRegister({ onRegistered }: Props) {
  const [name, setName] = useState('테스트 가맹점')
  const [loading, setLoading] = useState(false)

  const handleSubmit = async () => {
    if (!name.trim()) return
    setLoading(true)
    try {
      const merchant = await registerMerchant(name)
      onRegistered(merchant)
    } catch (e) {
      alert('등록 실패: ' + (e instanceof Error ? e.message : e))
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="section">
      <h2>1. 가맹점 등록</h2>
      <div className="form-group">
        <label>가맹점 이름:</label>
        <input
          type="text"
          value={name}
          onChange={e => setName(e.target.value)}
          onKeyDown={e => e.key === 'Enter' && handleSubmit()}
        />
        <button onClick={handleSubmit} disabled={loading}>
          {loading ? '등록 중...' : '등록'}
        </button>
      </div>
    </div>
  )
}
