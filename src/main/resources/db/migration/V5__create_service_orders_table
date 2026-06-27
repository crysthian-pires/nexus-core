CREATE TABLE IF NOT EXISTS tb_service_orders (
    id              BIGSERIAL PRIMARY KEY,
    customer_id     BIGINT NOT NULL REFERENCES tb_customers(id),
    description     TEXT NOT NULL,
    status          VARCHAR(20) NOT NULL DEFAULT 'PENDENTE',
    total_value     NUMERIC(10, 2),
    notes           TEXT,
    completed_at    TIMESTAMP,
    created_at      TIMESTAMP NOT NULL,
    updated_at      TIMESTAMP
);
