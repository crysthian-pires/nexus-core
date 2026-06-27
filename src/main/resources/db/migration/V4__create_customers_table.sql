CREATE TABLE IF NOT EXISTS tb_customers (
                                            id          BIGSERIAL PRIMARY KEY,
                                            name        VARCHAR(255) NOT NULL,
    email       VARCHAR(255),
    phone       VARCHAR(20),
    document    VARCHAR(20),
    notes       TEXT,
    active      BOOLEAN NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP NOT NULL,
    updated_at  TIMESTAMP
    );
