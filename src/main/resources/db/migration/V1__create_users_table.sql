CREATE TABLE IF NOT EXISTS tb_users (
                                        id          BIGSERIAL PRIMARY KEY,
                                        name        VARCHAR(255) NOT NULL,
    email       VARCHAR(255) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    cpf         VARCHAR(14),
    phone       VARCHAR(20),
    birth_date  DATE,
    role        VARCHAR(20)  NOT NULL DEFAULT 'USER',
    active      BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP    NOT NULL,
    updated_at  TIMESTAMP
    );
