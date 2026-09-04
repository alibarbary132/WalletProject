-- Users table: login identities, separate from Account (which represents a wallet/balance).
CREATE TABLE app_user (
                          id              BIGSERIAL PRIMARY KEY,
                          username        VARCHAR(50)  NOT NULL,
                          email           VARCHAR(255) NOT NULL,
                          password_hash   VARCHAR(255) NOT NULL,
                          role            VARCHAR(20)  NOT NULL DEFAULT 'USER',   -- USER, ADMIN
                          created_at      TIMESTAMP    NOT NULL DEFAULT now(),

                          CONSTRAINT uq_app_user_username UNIQUE (username),
                          CONSTRAINT uq_app_user_email UNIQUE (email)
);

-- Link each account to the user who owns it.
ALTER TABLE account ADD COLUMN user_id BIGINT REFERENCES app_user(id);

CREATE INDEX idx_account_user ON account(user_id);