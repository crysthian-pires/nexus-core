CREATE TABLE IF NOT EXISTS tb_appointments (
                                               id                  BIGSERIAL PRIMARY KEY,
                                               customer_id         BIGINT NOT NULL REFERENCES tb_customers(id),
    service_order_id    BIGINT REFERENCES tb_service_orders(id),
    description         TEXT NOT NULL,
    scheduled_at        TIMESTAMP NOT NULL,
    status              VARCHAR(20) NOT NULL DEFAULT 'AGENDADO',
    estimated_value     NUMERIC(10, 2),
    notes               TEXT,
    created_at          TIMESTAMP NOT NULL,
    updated_at          TIMESTAMP
    );
