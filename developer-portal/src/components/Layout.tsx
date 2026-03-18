import { ReactNode } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import './Layout.css'

interface LayoutProps {
  children: ReactNode
}

function Layout({ children }: LayoutProps) {
  const { isAuthenticated, clientName, logout } = useAuth()
  const navigate = useNavigate()

  const handleLogout = () => {
    logout()
    navigate('/')
  }

  return (
    <div className="layout">
      <header className="header">
        <div className="header-content">
          <Link to="/" className="logo">
            HaruPay 개발자 센터
          </Link>
          <nav className="nav">
            <Link to="/" className="nav-link">홈</Link>
            <Link to="/docs" className="nav-link">API 문서</Link>
            {isAuthenticated ? (
              <>
                <span className="nav-user">{clientName}님</span>
                <button onClick={handleLogout} className="nav-link">
                  로그아웃
                </button>
              </>
            ) : (
              <>
                <Link to="/login" className="nav-link">
                  로그인
                </Link>
                <Link to="/register" className="nav-link nav-link-primary">
                  클라이언트 등록
                </Link>
              </>
            )}
          </nav>
        </div>
      </header>
      
      <main className="main">
        {children}
      </main>
      
      <footer className="footer">
        <p>© 2024 HaruPay. All rights reserved.</p>
      </footer>
    </div>
  )
}

export default Layout
