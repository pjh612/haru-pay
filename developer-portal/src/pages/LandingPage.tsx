import { Link } from 'react-router-dom'
import './LandingPage.css'

function LandingPage() {
  return (
    <div className="landing">
      <section className="hero">
        <h1>HaruPay 개발자 센터</h1>
        <p className="hero-subtitle">
          간편하고 안전한 결제 통합을 시작하세요
        </p>
        <div className="hero-cta">
          <Link to="/register" className="btn btn-primary">
            클라이언트 등록하기
          </Link>
          <Link to="/docs" className="btn btn-secondary">
            API 문서 보기
          </Link>
        </div>
      </section>

      <section className="features">
        <h2>주요 기능</h2>
        <div className="feature-grid">
          <div className="feature-card">
            <div className="feature-icon">🔑</div>
            <h3>API 키 발급</h3>
            <p>간단한 등록으로 즉시 API 키를 발급받아 통합을 시작하세요.</p>
          </div>
          
          <div className="feature-card">
            <div className="feature-icon">📊</div>
            <h3>실시간 모니터링</h3>
            <p>대시보드에서 결제 현황과 API 사용량을 실시간으로 확인하세요.</p>
          </div>
          
          <div className="feature-card">
            <div className="feature-icon">📚</div>
            <h3>상세한 문서</h3>
            <p>완벽한 API 문서와 예제 코드로 쉽게 통합하세요.</p>
          </div>
          
          <div className="feature-card">
            <div className="feature-icon">🛡️</div>
            <h3>안전한 보안</h3>
            <p>멱등성 키와 암호화된 통신으로 안전한 결제를 보장합니다.</p>
          </div>
        </div>
      </section>

      <section className="quick-start">
        <h2>빠른 시작</h2>
        <div className="steps">
          <div className="step">
            <div className="step-number">1</div>
            <h3>클��이언트 등록</h3>
            <p>상점 정보를 입력하고 API 키를 발급받으세요.</p>
          </div>
          
          <div className="step">
            <div className="step-number">2</div>
            <h3>SDK 설치</h3>
            <p>npm install @harupay/sdk로 SDK를 설치하세요.</p>
          </div>
          
          <div className="step">
            <div className="step-number">3</div>
            <h3>결제 연동</h3>
            <p>API 키를 설정하고 결제 위젯을 초기화하세요.</p>
          </div>
        </div>
      </section>
    </div>
  )
}

export default LandingPage
