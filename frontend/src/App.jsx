import { useState } from 'react'
import { clearToken, getToken, setToken, usernameFromToken } from './api'
import AuthPage from './components/AuthPage'
import Dashboard from './components/Dashboard'

export default function App() {
  const [token, setTokenState] = useState(getToken())

  const signIn = (t) => {
    setToken(t)
    setTokenState(t)
  }
  const signOut = () => {
    clearToken()
    setTokenState(null)
  }

  if (!token) return <AuthPage onSignedIn={signIn} />
  return <Dashboard username={usernameFromToken(token) ?? 'user'} onSignOut={signOut} />
}
