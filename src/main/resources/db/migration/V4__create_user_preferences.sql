CREATE TABLE users.user_preferences (
    user_id            BIGINT PRIMARY KEY REFERENCES users.users (id),
    max_idle_minutes   INT         NOT NULL DEFAULT 30,
    timezone           VARCHAR(50) NOT NULL DEFAULT 'America/Argentina/Buenos_Aires',
    primary_currency   VARCHAR(3)  NOT NULL DEFAULT 'ARS',
    secondary_currency VARCHAR(10),
    number_format      VARCHAR(10) NOT NULL DEFAULT 'es-AR',
    decimals           INT         NOT NULL DEFAULT 2,
    color_for_amounts  BOOLEAN     NOT NULL DEFAULT true,
    updated_at         TIMESTAMP   NOT NULL DEFAULT now()
);
