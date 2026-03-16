import type { MerchantSession } from '../types'
import { clearSession } from '../api/client'

interface Props {
  merchant: MerchantSession
  onCleared: () => void
}

export default function MerchantInfo({ merchant, onCleared }: Props) {
  const handleClear = async () => {
    await clearSession()
    onCleared()
  }

  return (
    <div className="section">
      <h2>가맹점 정보</h2>
      <div className="info-box">
        <p><strong>가맹점 ID:</strong> {merchant.clientId}</p>
        <p><strong>가맹점 이름:</strong> {merchant.clientName}</p>
        <p><strong>API Key:</strong> {merchant.apiKey.substring(0, 10)}...</p>
        <button onClick={handleClear}>세션 초기화</button>
      </div>
    </div>
  )
}
