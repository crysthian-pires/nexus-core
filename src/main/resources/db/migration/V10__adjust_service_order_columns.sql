ALTER TABLE tb_service_orders
ALTER COLUMN description TYPE VARCHAR(2000);

ALTER TABLE tb_service_orders
ALTER COLUMN total_value TYPE NUMERIC(12, 2);

ALTER TABLE tb_service_orders
ALTER COLUMN notes TYPE VARCHAR(1000);
