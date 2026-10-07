import { useCallback, useEffect, useState } from 'react'
import { api, money } from '../api'
import AccountCard from './AccountCard'
import TransactionForm from './TransactionForm'

// The API has no "list my accounts" or "list transactions" endpoints and accounts
// aren't linked to users, so each browser remembers the account ids/history it has used.
function useStored(key, initial) {
  const [value, setValue] = useState(() => {
    try { return JSON.parse(localStorage.getItem(key)) ?? initial } catch { return initial }
  })
  useEffect(() => { localStorage.setItem(key, JSON.stringify(value)) }, [key, value])
  return [value, setValue]
}

export default function Dashboard({ username, onSignOut }) {
  const [ids, setIds] = useStored(`wallet.accounts.${username}`, [])
  const [history, setHistory] = useStored(`wallet.history.${username}`, [])
  const [accounts, setAccounts] = useState({}) // id -> account | { error }
  const [newOwner, setNewOwner] = useState(username)
  const [existingId, setExistingId] = useState('')
  const [error, setError] = useState('')

  const guard = useCallback((err) => {
    if (err.status === 401 || err.status === 403) onSignOut()
    else setError(err.message)
  }, [onSignOut])

  const refresh = useCallback(async (list) => {
    const results = await Promise.all(
      list.map((id) => api.getAccount(id).then((a) => [id, a]).catch((e) => {
        if (e.status === 401 || e.status === 403) onSignOut()
        return [id, { error: e.message }]
      })),
    )
    setAccounts(Object.fromEntries(results))
  }, [onSignOut])

  useEffect(() => { refresh(ids) }, [ids, refresh])

  async function createAccount(e) {
    e.preventDefault()
    setError('')
    try {
      const a = await api.createAccount(newOwner.trim())
      setIds((cur) => [...cur, a.id])
    } catch (err) { guard(err) }
  }

  async function addExisting(e) {
    e.preventDefault()
    setError('')
    const id = Number(existingId)
    if (!Number.isInteger(id) || id <= 0) return setError('Enter a valid account id.')
    if (ids.includes(id)) return setError('That account is already on your list.')
    try {
      await api.getAccount(id)
      setIds((cur) => [...cur, id])
      setExistingId('')
    } catch (err) { guard(err) }
  }

  const removeId = (id) => setIds((cur) => cur.filter((x) => x !== id))

  const onDone = (tx) => {
    setHistory((h) => [tx, ...h].slice(0, 50))
    refresh(ids)
  }

  const total = Object.values(accounts).reduce((s, a) => s + (a.balance ? Number(a.balance) : 0), 0)

  return (
    <div className="shell">
      <header>
        <h1>Wallet</h1>
        <div className="who">
          <span>{username}</span>
          <button className="secondary" onClick={onSignOut}>Sign out</button>
        </div>
      </header>

      {error && <p className="error" role="alert">{error}</p>}

      <section>
        <div className="section-head">
          <h2>Accounts</h2>
          {ids.length > 0 && <span className="muted">Total {money(total)}</span>}
        </div>
        <div className="grid">
          {ids.map((id) => (
            <AccountCard key={id} id={id} account={accounts[id]} onRemove={() => removeId(id)} />
          ))}
          {ids.length === 0 && <p className="muted">No accounts yet — create one below.</p>}
        </div>

        <div className="row">
          <form onSubmit={createAccount} className="inline">
            <input value={newOwner} onChange={(e) => setNewOwner(e.target.value)} placeholder="Owner name" required />
            <button>Create account</button>
          </form>
          <form onSubmit={addExisting} className="inline">
            <input value={existingId} onChange={(e) => setExistingId(e.target.value)} placeholder="Existing account id" inputMode="numeric" />
            <button className="secondary">Add by id</button>
          </form>
        </div>
      </section>

      <section>
        <h2>Move money</h2>
        <TransactionForm ids={ids} onDone={onDone} />
      </section>

      <section>
        <h2>Recent activity</h2>
        {history.length === 0 ? (
          <p className="muted">Transactions you make here will appear below.</p>
        ) : (
          <table>
            <thead>
              <tr><th>#</th><th>Type</th><th>From</th><th>To</th><th className="num">Amount</th><th>Status</th><th>When</th></tr>
            </thead>
            <tbody>
              {history.map((t) => (
                <tr key={t.id}>
                  <td>{t.id}</td>
                  <td><span className={`badge ${t.type.toLowerCase()}`}>{t.type}</span></td>
                  <td>{t.sourceAccount ?? '—'}</td>
                  <td>{t.destinationA ?? '—'}</td>
                  <td className="num">{money(t.amount)}</td>
                  <td>{t.status}</td>
                  <td>{new Date(t.dateTime).toLocaleString()}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </section>
    </div>
  )
}
