CREATE TABLE users.user_sessions (
    id               BIGSERIAL PRIMARY KEY,
    user_id          BIGINT       NOT NULL REFERENCES users.users (id),
    refresh_token_id UUID         NOT NULL UNIQUE,
    device           VARCHAR(100) NOT NULL,
    remember_me      BOOLEAN      NOT NULL DEFAULT false,
    created_at       TIMESTAMP    NOT NULL DEFAULT now(),
    last_seen_at     TIMESTAMP    NOT NULL DEFAULT now(),
    revoked          BOOLEAN      NOT NULL DEFAULT false
);
CREATE INDEX idx_user_sessions_user ON users.user_sessions (user_id);
