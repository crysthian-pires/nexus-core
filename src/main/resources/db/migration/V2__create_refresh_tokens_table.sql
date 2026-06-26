CREATE TABLE IF NOT EXISTS tb_refresh_tokens (
                                                 id          BIGSERIAL PRIMARY KEY,
                                                 token       VARCHAR(255) NOT NULL UNIQUE,
    user_id     BIGINT NOT NULL REFERENCES tb_users(id),
    expires_at  TIMESTAMP NOT NULL,
    created_at  TIMESTAMP NOT NULL
    );
