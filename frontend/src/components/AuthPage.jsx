import { useState } from 'react'
import { api } from '../api'

export default function AuthPage({ onSignedIn }) {
  const [mode, setMode] = useState('login')
  const [form, setForm] = useState({ username: '', email: '', password: '' })
  const [error, setError] = useState('')
  const [busy, setBusy] = useState(false)

  const set = (k) => (e) => setForm({ ...form, [k]: e.target.value })

  async function submit(e) {
    e.preventDefault()
    setError('')
    setBusy(true)
    try {
      if (mode === 'register') {
        await api.register(form.username, form.email, form.password)
      }
      const { token } = await api.login(form.username, form.password)
      onSignedIn(token)
    } catch (err) {
      setError(
        mode === 'login' && err.status >= 400 && err.status !== 0
          ? 'Invalid username or password.'
          : err.message,
      )
    } finally {
      setBusy(false)
    }
  }

  return (
    <main className="auth">
      <form className="card" onSubmit={submit}>
        <h1>Wallet</h1>
        <p className="muted">{mode === 'login' ? 'Sign in to your wallet' : 'Create your account'}</p>

        <label>Username
          <input value={form.username} onChange={set('username')} required autoFocus />
        </label>
        {mode === 'register' && (
          <label>Email
            <input type="email" value={form.email} onChange={set('email')} required />
          </label>
        )}
        <label>Password
          <input type="password" value={form.password} onChange={set('password')} required />
        </label>

        {error && <p className="error" role="alert">{error}</p>}
        <button disabled={busy}>{busy ? 'Please wait…' : mode === 'login' ? 'Sign in' : 'Register'}</button>

        <p className="muted switch">
          {mode === 'login' ? 'No account yet? ' : 'Already registered? '}
          <button type="button" className="link" onClick={() => { setMode(mode === 'login' ? 'register' : 'login'); setError('') }}>
            {mode === 'login' ? 'Register' : 'Sign in'}
          </button>
        </p>
      </form>
    </main>
  )
}
