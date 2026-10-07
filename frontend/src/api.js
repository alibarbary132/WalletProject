const BASE = '/api'
const TOKEN_KEY = 'wallet.token'

export const getToken = () => localStorage.getItem(TOKEN_KEY)
export const setToken = (t) => localStorage.setItem(TOKEN_KEY, t)
export const clearToken = () => localStorage.removeItem(TOKEN_KEY)

export class ApiError extends Error {
  constructor(message, status) {
    super(message)
    this.status = status
  }
}

async function request(path, { method = 'GET', body } = {}) {
  const headers = { 'Content-Type': 'application/json' }
  const token = getToken()
  if (token) headers.Authorization = `Bearer ${token}`

  let res
  try {
    res = await fetch(BASE + path, { method, headers, body: body && JSON.stringify(body) })
  } catch {
    throw new ApiError('Cannot reach the server. Is the backend running on :8080?', 0)
  }

  const text = await res.text()
  const data = text ? safeJson(text) : null
  if (!res.ok) {
    const message = data?.detail || data?.message || defaultMessage(res.status)
    throw new ApiError(message, res.status)
  }
  return data
}

const safeJson = (t) => {
  try { return JSON.parse(t) } catch { return null }
}

const defaultMessage = (status) =>
  status === 401 || status === 403 ? 'Not authorised. Please sign in again.'
    : status === 400 ? 'The server rejected that request.'
    : `Request failed (${status}).`

export const api = {
  register: (username, email, password) =>
    request('/auth/register', { method: 'POST', body: { username, email, password } }),
  login: (username, password) =>
    request('/auth/login', { method: 'POST', body: { username, password } }),
  createAccount: (ownerName) =>
    request('/accounts/create', { method: 'POST', body: { ownerName } }),
  getAccount: (id) => request(`/accounts/${id}`),
  deposit: (id, amount) =>
    request(`/transactions/accountId/${id}/deposit`, { method: 'POST', body: { amount } }),
  withdraw: (id, amount) =>
    request(`/transactions/accountId/${id}/withdraw`, { method: 'POST', body: { amount } }),
  transfer: (sourceAccount, destinationAccount, amount) =>
    request('/transactions/transfer', {
      method: 'POST',
      body: { amount, type: 'TRANSFER', sourceAccount, destinationAccount },
    }),
}

export function usernameFromToken(token) {
  try {
    const payload = token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/')
    return JSON.parse(atob(payload)).sub ?? null
  } catch {
    return null
  }
}

export const money = (v) =>
  new Intl.NumberFormat(undefined, { style: 'currency', currency: 'USD' }).format(Number(v))
