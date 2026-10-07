# Wallet frontend

React (Vite) UI for the WalletProject API: register/sign in, create accounts, deposit, withdraw and transfer.

```bash
npm install
npm run dev      # http://localhost:5173
```

The backend must be running on `localhost:8080`. The dev server proxies `/api/*` to it (see `vite.config.js`), so no CORS setup is needed.

The API has no endpoints to list accounts or transactions, and accounts aren't linked to users, so the UI remembers the account ids and recent activity you use in this browser (`localStorage`, per username).
