import { useCallback, useEffect, useState } from 'react'
import type { MerchantSession, PreparedPayment } from './types'
import { confirmPayment, getMerchant, getPayments } from './api/client'
import MerchantRegister from './components/MerchantRegister'
import MerchantInfo from './components/MerchantInfo'
import PaymentPrepare from './components/PaymentPrepare'
import PaymentList from './components/PaymentList'
import EventStream from './components/EventStream'

export default function App() {
  const [merchant, setMerchant] = useState<MerchantSession | null>(null)
  const [payments, setPayments] = useState<PreparedPayment[]>([])
  const [streamPaymentId, setStreamPaymentId] = useState<string | null>(null)
  const [loading, setLoading] = useState(true)

  const refreshPayments = useCallback(async () => {
    const list = await getPayments()
    setPayments(list)
  }, [])

  useEffect(() => {
    getMerchant()
      .then(m => {
        setMerchant(m)
        if (m) return refreshPayments()
      })
      .finally(() => setLoading(false))
  }, [refreshPayments])

  const handleConfirm = async (paymentId: string) => {
    try {
      await confirmPayment(paymentId)
      setPayments(prev =>
        prev.map(p => p.paymentId === paymentId ? { ...p, status: 'CONFIRMING' as const } : p)
      )
    } catch (e) {
      alert('확정 실패: ' + (e instanceof Error ? e.message : e))
    }
  }

  const handleCleared = () => {
    setMerchant(null)
    setPayments([])
    setStreamPaymentId(null)
  }

  if (loading) return <p>로딩 중...</p>

  return (
    <div>
      <h1>하루페이 (Haru Pay) 테스트 클라이언트</h1>

      {!merchant ? (
        <MerchantRegister onRegistered={m => { setMerchant(m); refreshPayments() }} />
      ) : (
        <>
          <MerchantInfo merchant={merchant} onCleared={handleCleared} />
          <PaymentPrepare onPrepared={refreshPayments} />
          <PaymentList
            payments={payments}
            onConfirm={handleConfirm}
            onStream={setStreamPaymentId}
          />
          {streamPaymentId && <EventStream paymentId={streamPaymentId} />}
        </>
      )}
    </div>
  )
}
