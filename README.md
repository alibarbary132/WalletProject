# WalletProject

A small backend service for managing wallets: create accounts, deposit, withdraw, and transfer funds between them, with every balance-changing action recorded as a `Transaction`. Built with Spring Boot, PostgreSQL, and Flyway.

## Stack

- Java 17, Spring Boot (Web, Data JPA, Security)
- PostgreSQL, managed with Flyway migrations
- JWT-based authentication
- Maven

## Running it locally

**Requirements:** JDK 17+, Docker (for Postgres).

```bash
docker-compose up -d
./mvnw spring-boot:run
```

The app starts on `http://localhost:8080`. Flyway applies migrations automatically on startup.

## Frontend

A React (Vite) UI lives in [`frontend/`](frontend/). With the backend running:

```bash
cd frontend && npm install && npm run dev
```

## API overview

### Auth
- `POST /auth/register` — create a user (`username`, `email`, `password`)
- `POST /auth/login` — returns a JWT (`username`, `password`)

### Accounts
- `POST /accounts/create` — create an account (`ownerName`)
- `GET /accounts/{id}` — fetch account details
- `GET /accounts/{id}/balance` — fetch current balance

### Transactions
- `POST /transactions/accountId/{accountId}/deposit` — deposit into an account
- `POST /transactions/accountId/{accountId}/withdraw` — withdraw from an account (rejected with `409` if funds are insufficient)
- `POST /transactions/transfer` — move funds between two accounts, atomically


## Design notes

- All monetary values use `BigDecimal` with fixed precision/scale (`NUMERIC(19,4)`) to avoid floating-point rounding errors.
- Every deposit/withdrawal/transfer is wrapped in a single transaction, so a transfer either fully succeeds or fully rolls back — it never leaves one account debited without the other credited.
- `Account` uses JPA optimistic locking (`@Version`) so two concurrent updates to the same account can't silently overwrite each other's balance change.
- Domain errors (`AccountNotFoundException`, `InsufficientBalanceException`) are mapped to proper HTTP status codes and RFC 7807 `ProblemDetail` responses via a global exception handler, instead of leaking stack traces.
- Service-layer logic is covered by Mockito unit tests (account lookup, balance retrieval, withdrawal behavior).

## Known limitations / what's next

This is a portfolio project, not a production system. Things I'm aware of and would tackle next:

- **Auth is in progress:** JWT login/registration exists, but the `UserDetailsService` wiring isn't complete yet, so protected endpoints aren't fully enforced end-to-end.
- **Validation:** request DTOs don't yet enforce constraints (e.g. positive amounts, non-blank names) via Bean Validation.
- **Idempotency:** deposits/withdrawals don't yet dedupe retried requests by an idempotency key.
- **Test coverage:** unit tests cover the core service logic; integration tests (e.g. with Testcontainers) and tests for insufficient-balance/transfer edge cases are next.
