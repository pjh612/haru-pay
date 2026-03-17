import { useState } from 'react'
import { preparePayment } from '../api/client'

interface Props {
  onPrepared: (paymentId: string) => void
}

export default function PaymentPrepare({ onPrepared }: Props) {
  const [orderId, setOrderId] = useState('')
  const [productName, setProductName] = useState('')
  const [requestPrice, setRequestPrice] = useState(10000)
  const [loading, setLoading] = useState(false)

  const handleSubmit = async () => {
    if (!orderId.trim() || !productName.trim()) {
      alert('주문 ID와 상품명을 입력해주세요')
      return
    }
    setLoading(true)
    try {
      const payment = await preparePayment(orderId, productName, requestPrice)
      setOrderId('')
      setProductName('')
      onPrepared(payment.paymentId)
    } catch (e) {
      alert('준비 실패: ' + (e instanceof Error ? e.message : e))
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="section">
      <h2>2. 결제 주문 생성</h2>
      <div className="form-group">
        <label>주문 ID:</label>
        <input type="text" value={orderId} onChange={e => setOrderId(e.target.value)} placeholder="ORDER-001" />
      </div>
      <div className="form-group">
        <label>상품명:</label>
        <input type="text" value={productName} onChange={e => setProductName(e.target.value)} placeholder="테스트 상품" />
      </div>
      <div className="form-group">
        <label>결제 금액:</label>
        <input type="number" value={requestPrice} onChange={e => setRequestPrice(Number(e.target.value))} />
      </div>
      <button onClick={handleSubmit} disabled={loading}>
        {loading ? '생성 중...' : '주문 생성'}
      </button>
    </div>
  )
}
