import { money } from '../api'

export default function AccountCard({ id, account, onRemove }) {
  return (
    <div className="card account">
      <div className="section-head">
        <span className="muted">Account #{id}</span>
        <button className="link" onClick={onRemove} title="Remove from list (does not delete the account)">remove</button>
      </div>
      {!account ? (
        <p className="muted">Loading…</p>
      ) : account.error ? (
        <p className="error">{account.error}</p>
      ) : (
        <>
          <strong className="owner">{account.ownerName}</strong>
          <span className="balance">{money(account.balance)}</span>
        </>
      )}
    </div>
  )
}
