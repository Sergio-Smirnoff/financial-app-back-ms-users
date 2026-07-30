CREATE TABLE users.manual_currency_rates (
    id           BIGSERIAL PRIMARY KEY,
    user_id      BIGINT        NOT NULL REFERENCES users.users (id),
    currency     VARCHAR(3)    NOT NULL,
    rate_per_ars NUMERIC(18,6) NOT NULL,
    updated_at   TIMESTAMP     NOT NULL DEFAULT now(),
    UNIQUE (user_id, currency)
);
