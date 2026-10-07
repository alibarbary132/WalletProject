import { useEffect, useState } from 'react'
import { api } from '../api'

const TABS = ['deposit', 'withdraw', 'transfer']

export default function TransactionForm({ ids, onDone }) {
  const [tab, setTab] = useState('deposit')
  const [source, setSource] = useState('')
  const [dest, setDest] = useState('')
  const [amount, setAmount] = useState('')
  const [message, setMessage] = useState(null) // { kind, text }
  const [busy, setBusy] = useState(false)

  useEffect(() => {
    if (!ids.includes(Number(source))) setSource(ids[0] ?? '')
  }, [ids, source])

  async function submit(e) {
    e.preventDefault()
    setMessage(null)
    const value = Number(amount)
    if (!(value > 0)) return setMessage({ kind: 'error', text: 'Amount must be greater than zero.' })
    if (!source) return setMessage({ kind: 'error', text: 'Add an account first.' })
    if (tab === 'transfer') {
      if (!dest) return setMessage({ kind: 'error', text: 'Enter a destination account id.' })
      if (Number(dest) === Number(source)) {
        return setMessage({ kind: 'error', text: 'Source and destination must differ.' })
      }
    }

    setBusy(true)
    try {
      const tx = tab === 'deposit' ? await api.deposit(source, value)
        : tab === 'withdraw' ? await api.withdraw(source, value)
        : await api.transfer(Number(source), Number(dest), value)
      setMessage({ kind: 'ok', text: `${tx.type} of ${value.toFixed(2)} completed (transaction #${tx.id}).` })
      setAmount('')
      onDone(tx)
    } catch (err) {
      setMessage({ kind: 'error', text: err.message })
    } finally {
      setBusy(false)
    }
  }

  return (
    <form className="card tx" onSubmit={submit}>
      <div className="tabs" role="tablist">
        {TABS.map((t) => (
          <button
            type="button"
            key={t}
            role="tab"
            aria-selected={tab === t}
            className={tab === t ? 'tab active' : 'tab'}
            onClick={() => { setTab(t); setMessage(null) }}
          >
            {t}
          </button>
        ))}
      </div>

      <div className="row">
        <label>{tab === 'transfer' ? 'From' : 'Account'}
          <select value={source} onChange={(e) => setSource(e.target.value)} disabled={ids.length === 0}>
            {ids.map((id) => <option key={id} value={id}>#{id}</option>)}
          </select>
        </label>
        {tab === 'transfer' && (
          <label>To (account id)
            <input value={dest} onChange={(e) => setDest(e.target.value)} inputMode="numeric" required />
          </label>
        )}
        <label>Amount
          <input type="number" min="0.01" step="0.01" value={amount}
            onChange={(e) => setAmount(e.target.value)} required />
        </label>
        <button disabled={busy || ids.length === 0}>{busy ? 'Working…' : tab}</button>
      </div>

      {message && <p className={message.kind === 'ok' ? 'ok' : 'error'} role="status">{message.text}</p>}
    </form>
  )
}
