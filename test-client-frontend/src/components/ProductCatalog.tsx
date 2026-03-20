import { useState } from 'react'

interface Product {
  name: string
  price: number
  description: string
}

const PRODUCTS: Product[] = [
  { name: '베이직 플랜', price: 9900, description: '기본 구독 플랜' },
  { name: '프로 플랜', price: 29900, description: '프리미엄 구독 플랜' },
  { name: '테스트 상품', price: 1000, description: '테스트용 소액 결제' },
]

interface Props {
  onBuy: (productName: string, price: number) => Promise<void>
}

export default function ProductCatalog({ onBuy }: Props) {
  const [loadingIdx, setLoadingIdx] = useState<number | null>(null)

  const handleBuy = async (product: Product, idx: number) => {
    setLoadingIdx(idx)
    try {
      await onBuy(product.name, product.price)
    } catch (e) {
      alert('결제 준비 실패: ' + (e instanceof Error ? e.message : e))
    } finally {
      setLoadingIdx(null)
    }
  }

  return (
    <div className="section product-section">
      <div className="section-head">
        <h2>상품 선택</h2>
        <p>구매할 상품을 선택하면 결제창이 열립니다.</p>
      </div>
      <div className="product-grid">
        {PRODUCTS.map((product, idx) => (
          <div key={idx} className="product-card">
            <p className="product-tag">구독 상품</p>
            <h3>{product.name}</h3>
            <p className="product-desc">{product.description}</p>
            <p className="product-price">{product.price.toLocaleString()}원</p>
            <button
              className="btn-primary"
              onClick={() => handleBuy(product, idx)}
              disabled={loadingIdx !== null}
            >
              {loadingIdx === idx ? '준비 중...' : '결제하기'}
            </button>
          </div>
        ))}
      </div>
    </div>
  )
}
