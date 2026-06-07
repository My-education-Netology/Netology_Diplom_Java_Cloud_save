CREATE TABLE users (
    id            BIGSERIAL PRIMARY KEY,
    login         VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    created_at    TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE auth_tokens (
    id         BIGSERIAL PRIMARY KEY,
    user_id    BIGINT       NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    token      VARCHAR(255) NOT NULL UNIQUE,
    active     BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE stored_files (
    id           BIGSERIAL PRIMARY KEY,
    user_id      BIGINT        NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    filename     VARCHAR(500)  NOT NULL,
    size         BIGINT        NOT NULL,
    storage_path VARCHAR(1000) NOT NULL,
    created_at   TIMESTAMP     NOT NULL DEFAULT NOW(),
    edited_at    TIMESTAMP     NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_user_filename UNIQUE (user_id, filename)
);

CREATE INDEX idx_auth_tokens_token ON auth_tokens (token) WHERE active = TRUE;
CREATE INDEX idx_stored_files_user_id ON stored_files (user_id);
