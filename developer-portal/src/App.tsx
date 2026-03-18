import { BrowserRouter as Router, Routes, Route } from 'react-router-dom'
import Layout from './components/Layout'
import LandingPage from './pages/LandingPage'
import RegisterPage from './pages/RegisterPage'
import DashboardPage from './pages/DashboardPage'
import ApiDocsPage from './pages/ApiDocsPage'

function App() {
  return (
    <Router>
      <Layout>
        <Routes>
          <Route path="/" element={<LandingPage />} />
          <Route path="/register" element={<RegisterPage />} />
          <Route path="/dashboard/:clientId" element={<DashboardPage />} />
          <Route path="/docs" element={<ApiDocsPage />} />
        </Routes>
      </Layout>
    </Router>
  )
}

export default App
