ALTER TABLE tb_appointments
ALTER COLUMN description TYPE VARCHAR(500);

ALTER TABLE tb_appointments
ALTER COLUMN estimated_value TYPE NUMERIC(12, 2);

ALTER TABLE tb_appointments
ALTER COLUMN notes TYPE VARCHAR(1000);
