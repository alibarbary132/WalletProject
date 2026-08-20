-- Accounts table: represents a wallet. Balance is only ever changed via transactions.
CREATE TABLE account (
                         id              BIGSERIAL PRIMARY KEY,
                         owner_name      VARCHAR(255) NOT NULL,
                         balance         NUMERIC(19, 4) NOT NULL DEFAULT 0,
                         created_at      TIMESTAMP NOT NULL DEFAULT now()
);

-- Transactions table: every balance-changing action is recorded here.
CREATE TABLE transaction (
                             id                      BIGSERIAL PRIMARY KEY,
                             type                    VARCHAR(20) NOT NULL,      -- DEPOSIT, WITHDRAWAL, TRANSFER
                             amount                  NUMERIC(19, 4) NOT NULL,
                             source_account_id       BIGINT REFERENCES account(id),
                             destination_account_id  BIGINT REFERENCES account(id),
                             status                  VARCHAR(20) NOT NULL,      -- PENDING, COMPLETED, FAILED
                             idempotency_key         VARCHAR(255) NOT NULL,
                             created_at              TIMESTAMP NOT NULL DEFAULT now(),

                             CONSTRAINT uq_transaction_idempotency_key UNIQUE (idempotency_key)
);

-- Speeds up "has this account done any transactions" / history lookups
CREATE INDEX idx_transaction_source_account ON transaction(source_account_id);
CREATE INDEX idx_transaction_destination_account ON transaction(destination_account_id);