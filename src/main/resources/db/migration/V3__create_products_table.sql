CREATE TABLE IF NOT EXISTS tb_products (
                                           id          BIGSERIAL PRIMARY KEY,
                                           name        VARCHAR(255) NOT NULL,
    description TEXT,
    price       NUMERIC(10, 2) NOT NULL,
    quantity    INTEGER NOT NULL DEFAULT 0,
    category    VARCHAR(100),
    active      BOOLEAN NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP NOT NULL,
    updated_at  TIMESTAMP
    );
