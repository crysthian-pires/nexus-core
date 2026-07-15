UPDATE tb_customers
SET document = regexp_replace(document, '\D', '', 'g')
WHERE document IS NOT NULL;

ALTER TABLE tb_customers
ALTER COLUMN name TYPE VARCHAR(200);

ALTER TABLE tb_customers
ALTER COLUMN email TYPE VARCHAR(254);

ALTER TABLE tb_customers
ALTER COLUMN phone TYPE VARCHAR(20);

ALTER TABLE tb_customers
ALTER COLUMN document TYPE VARCHAR(14);

ALTER TABLE tb_customers
ALTER COLUMN notes TYPE VARCHAR(1000);

ALTER TABLE tb_customers
    ADD CONSTRAINT uk_customers_document UNIQUE (document);
