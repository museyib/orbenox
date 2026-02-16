INSERT INTO user_type (code, name, created_by)
VALUES ('ADMIN', 'Administrators', 'system'),
       ('USER', 'Ordinary users', 'system'),
       ('GUEST', 'Guests', 'system');

INSERT INTO unit_dimension (code, name, created_by)
VALUES ('COUNT', 'Say', 'system'),
       ('MASS', 'Kütlə', 'system'),
       ('LENGTH', 'Uzunluq', 'system'),
       ('VOLUME', 'Həcm', 'system'),
       ('TEMPERATURE', 'Temperatur', 'system');

INSERT INTO unit (code, unit_dimension_id, base, factor_to_base, offset_to_base, name, created_by)
VALUES ('PCS', 1, TRUE, 1, 0, 'Ədəd', 'system'),
       ('KG', 2, FALSE, 1000, 0, 'Kiloqram', 'system'),
       ('G', 2, TRUE, 1, 0, 'Qram', 'system'),
       ('M', 3, TRUE, 1, 0, 'Metr', 'system'),
       ('L', 4, TRUE, 1, 0, 'Litr', 'system'),
       ('M3', 4, FALSE, 1000, 0, 'Kub metr', 'system'),
       ('C', 5, TRUE, 1, 0, 'Selsi', 'system'),
       ('K', 5, FALSE, 1, -273.15, 'Kelvin', 'system'),
       ('F', 5, FALSE, 5 / 9, 0, 'Farenheyt', 'system');

INSERT INTO currency (code, name, created_by)
VALUES ('AZN', 'Manat', 'system'),
       ('USD', 'Dollar', 'system'),
       ('EUR', 'Avro', 'system'),
       ('RUB', 'Rubl', 'system');

INSERT INTO country (code, name, created_by)
VALUES ('AZ', 'Azərbaycan', 'system'),
       ('TR', 'Türkiyə', 'system'),
       ('RU', 'Rusiya', 'system'),
       ('CH', 'Çin', 'system'),
       ('US', 'ABŞ', 'system'),
       ('EN', 'İngiltərə', 'system'),
       ('DE', 'Almaniya', 'system'),
       ('FR', 'Fransa', 'system'),
       ('IT', 'İtaliya', 'system'),
       ('ES', 'İspaniya', 'system');

INSERT INTO resource (code, name, created_by)
VALUES ('LOOKUP', 'Baxış', 'system'),
       ('APP_USER', 'İstifadəçilər', 'system'),
       ('APP_ROLE', 'Rollar', 'system'),
       ('APP_PERMISSION', 'İcazələr', 'system'),
       ('APP_USER_ROLE', 'İstifadəçi rolları', 'system'),
       ('ACTION', 'Əməliyyatlar', 'system'),
       ('RESOURCE', 'Obyektlər', 'system'),
       ('CURRENCY', 'Valyutalar', 'system'),
       ('UNIT', 'Ölçü vahidləri', 'system'),
       ('UNIT_DIMENSION', 'Kəmiyyətlər', 'system'),
       ('COUNTRY', 'Ölkələr', 'system'),
       ('BRAND', 'Brendlər', 'system'),
       ('PRODUCER', 'İstehsalçılar', 'system'),
       ('PRODUCT_TYPE', 'Mal tipləri', 'system'),
       ('PRODUCT_CLASS', 'Mal sinifləri', 'system'),
       ('PRODUCT_GROUP', 'Mal qrupları', 'system'),
       ('PRODUCT_CATEGORY', 'Mal kateqoriyarları', 'system'),
       ('PRICE', 'Qiymət növləri', 'system'),
       ('WAREHOUSE', 'Anbarlar', 'system'),
       ('PRODUCT', 'Mallar', 'system'),
       ('PRODUCT_PRICE', 'Mal qiymətləri', 'system'),
       ('PRODUCT_BARCODE', 'Mal barkodları', 'system'),
       ('PRODUCT_UNIT', 'Mal üzrə ölçü vahidləri', 'system'),
       ('PRODUCT_WAREHOUSE', 'Mal-anbar', 'system'),
       ('TRANSACTION_TYPE', 'Mal-anbar', 'system');

INSERT INTO resource_action (resource_id, action, created_by)
VALUES (1, 'READ', 'system'),
       (2, 'READ', 'system'),
       (2, 'CREATE', 'system'),
       (2, 'UPDATE', 'system'),
       (2, 'DELETE', 'system'),
       (3, 'READ', 'system'),
       (3, 'CREATE', 'system'),
       (3, 'UPDATE', 'system'),
       (3, 'DELETE', 'system'),
       (4, 'READ', 'system'),
       (4, 'CREATE', 'system'),
       (4, 'UPDATE', 'system'),
       (4, 'DELETE', 'system'),
       (5, 'READ', 'system'),
       (5, 'CREATE', 'system'),
       (5, 'UPDATE', 'system'),
       (5, 'DELETE', 'system'),
       (6, 'READ', 'system'),
       (6, 'CREATE', 'system'),
       (6, 'UPDATE', 'system'),
       (6, 'DELETE', 'system'),
       (7, 'READ', 'system'),
       (7, 'CREATE', 'system'),
       (7, 'UPDATE', 'system'),
       (7, 'DELETE', 'system'),
       (8, 'READ', 'system'),
       (8, 'CREATE', 'system'),
       (8, 'UPDATE', 'system'),
       (8, 'DELETE', 'system'),
       (9, 'READ', 'system'),
       (9, 'CREATE', 'system'),
       (9, 'UPDATE', 'system'),
       (9, 'DELETE', 'system'),
       (10, 'READ', 'system'),
       (10, 'CREATE', 'system'),
       (10, 'UPDATE', 'system'),
       (10, 'DELETE', 'system'),
       (11, 'READ', 'system'),
       (11, 'CREATE', 'system'),
       (11, 'UPDATE', 'system'),
       (11, 'DELETE', 'system'),
       (12, 'READ', 'system'),
       (12, 'CREATE', 'system'),
       (12, 'UPDATE', 'system'),
       (12, 'DELETE', 'system'),
       (13, 'READ', 'system'),
       (13, 'CREATE', 'system'),
       (13, 'UPDATE', 'system'),
       (13, 'DELETE', 'system'),
       (14, 'READ', 'system'),
       (14, 'CREATE', 'system'),
       (14, 'UPDATE', 'system'),
       (14, 'DELETE', 'system'),
       (15, 'READ', 'system'),
       (15, 'CREATE', 'system'),
       (15, 'UPDATE', 'system'),
       (15, 'DELETE', 'system'),
       (16, 'READ', 'system'),
       (16, 'CREATE', 'system'),
       (16, 'UPDATE', 'system'),
       (16, 'DELETE', 'system'),
       (17, 'READ', 'system'),
       (17, 'CREATE', 'system'),
       (17, 'UPDATE', 'system'),
       (17, 'DELETE', 'system'),
       (18, 'READ', 'system'),
       (18, 'CREATE', 'system'),
       (18, 'UPDATE', 'system'),
       (18, 'DELETE', 'system'),
       (19, 'READ', 'system'),
       (19, 'CREATE', 'system'),
       (19, 'UPDATE', 'system'),
       (19, 'DELETE', 'system'),
       (20, 'READ', 'system'),
       (20, 'CREATE', 'system'),
       (20, 'UPDATE', 'system'),
       (20, 'DELETE', 'system'),
       (21, 'READ', 'system'),
       (21, 'CREATE', 'system'),
       (21, 'UPDATE', 'system'),
       (21, 'DELETE', 'system'),
       (22, 'READ', 'system'),
       (22, 'CREATE', 'system'),
       (22, 'UPDATE', 'system'),
       (22, 'DELETE', 'system'),
       (23, 'READ', 'system'),
       (23, 'CREATE', 'system'),
       (23, 'UPDATE', 'system'),
       (23, 'DELETE', 'system'),
       (24, 'READ', 'system'),
       (24, 'CREATE', 'system'),
       (24, 'UPDATE', 'system'),
       (24, 'DELETE', 'system'),
       (25, 'READ', 'system'),
       (25, 'CREATE', 'system'),
       (25, 'UPDATE', 'system'),
       (25, 'DELETE', 'system');

INSERT INTO account(code, name, account_type, created_by)
VALUES ('1000', 'Cash', 'ASSET', 'system'),
       ('1200', 'Inventory', 'ASSET', 'system'),
       ('2000', 'Accounts Payable', 'EXPENSE', 'system'),
       ('2100', 'Accounts Receivable', 'INCOME', 'system'),
       ('3000', 'Sales Revenue', 'INCOME', 'system');

INSERT INTO business_partner(code, name, type, created_by)
VALUES ('PARTNER', 'Partner name', 'PERSON', 'system');

INSERT INTO price_list (code, name, currency_id, created_by)
VALUES ('PRICE_01', 'Price 1', (SELECT id FROM currency WHERE code = 'AZN'), 'system');

INSERT INTO brand (code, name, description, created_by)
VALUES ('BRAND_1', 'Brand 1', 'Brand 1', 'system');
INSERT INTO producer (code, name, description, created_by)
VALUES ('PRODUCER_1', 'producer 1', 'producer 1', 'system');
INSERT INTO product_type (code, name, description, created_by)
VALUES ('TYPE_1', 'product_type 1', 'product_type 1', 'system');
INSERT INTO product_class (code, name, description, created_by)
VALUES ('CLASS_1', 'product_class 1', 'product_class 1', 'system');
INSERT INTO product_group (code, name, description, created_by)
VALUES ('GROUP_1', 'product_group 1', 'product_group 1', 'system');
INSERT INTO product_category (code, name, description, created_by)
VALUES ('CATEGORY_1', 'product_category 1', 'product_category 1', 'system');
INSERT INTO product (code, name, description, default_barcode, created_by)
VALUES ('CATEGORY_1', 'product_category 1', 'product_category 1', '123456789', 'system');
INSERT INTO warehouse (code, name, created_by)
VALUES ('W001', 'warehouse 1', 'system');
INSERT INTO product_price (product_id, price_list_id, unit_id, created_by, discount_ratio_limit)
VALUES (1, 1, 1, 'system', 30);

INSERT INTO transaction_type(code, name, commercial_affected, stock_affected, accounting_affected, credit_limit_checked, approval_required,
                             created_by)
VALUES ('APPROVE', 'Product approve', false, true, false, false, false, 'system'),
       ('SALES_ORDER', 'Sales order', true, true, true, true, true, 'system');

INSERT INTO posting_rule(sequence, type_id, debit_account_id, credit_account_id, amount_source, partner_side)
VALUES (10,
        (SELECT id FROM transaction_type WHERE code = 'SALES_ORDER'),
        (SELECT id FROM account WHERE code = '2100'),
        (SELECT id FROM account WHERE code = '3000'),
        'TOTAL',
        'DEBIT');