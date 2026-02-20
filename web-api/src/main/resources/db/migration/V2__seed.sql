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
       ('TRANSACTION_TYPE', 'Mal-anbar', 'system'),
       ('ACCOUNT', 'Hesablar', 'system'),
       ('BUSINESS_PARTNER', 'Biznes partnyorlar', 'system'),
       ('POSTING_RULE', 'Hesab uçot qaydaları', 'system'),
       ('BUSINESS_PARTNER_ROLE', 'Biznes partnyor rolları', 'system'),
       ('STOCK_BALANCE', 'Anbar qalıqları', 'system');

INSERT INTO resource_action (resource_id, action, created_by)
VALUES ((SELECT id FROM resource WHERE code = 'LOOKUP'), 'READ', 'system'),
       ((SELECT id FROM resource WHERE code = 'APP_USER'), 'READ', 'system'),
       ((SELECT id FROM resource WHERE code = 'APP_USER'), 'CREATE', 'system'),
       ((SELECT id FROM resource WHERE code = 'APP_USER'), 'UPDATE', 'system'),
       ((SELECT id FROM resource WHERE code = 'APP_USER'), 'DELETE', 'system'),
       ((SELECT id FROM resource WHERE code = 'APP_ROLE'), 'READ', 'system'),
       ((SELECT id FROM resource WHERE code = 'APP_ROLE'), 'CREATE', 'system'),
       ((SELECT id FROM resource WHERE code = 'APP_ROLE'), 'UPDATE', 'system'),
       ((SELECT id FROM resource WHERE code = 'APP_ROLE'), 'DELETE', 'system'),
       ((SELECT id FROM resource WHERE code = 'APP_PERMISSION'), 'READ', 'system'),
       ((SELECT id FROM resource WHERE code = 'APP_PERMISSION'), 'CREATE', 'system'),
       ((SELECT id FROM resource WHERE code = 'APP_PERMISSION'), 'UPDATE', 'system'),
       ((SELECT id FROM resource WHERE code = 'APP_PERMISSION'), 'DELETE', 'system'),
       ((SELECT id FROM resource WHERE code = 'APP_USER_ROLE'), 'READ', 'system'),
       ((SELECT id FROM resource WHERE code = 'APP_USER_ROLE'), 'CREATE', 'system'),
       ((SELECT id FROM resource WHERE code = 'APP_USER_ROLE'), 'UPDATE', 'system'),
       ((SELECT id FROM resource WHERE code = 'APP_USER_ROLE'), 'DELETE', 'system'),
       ((SELECT id FROM resource WHERE code = 'RESOURCE'), 'READ', 'system'),
       ((SELECT id FROM resource WHERE code = 'RESOURCE'), 'CREATE', 'system'),
       ((SELECT id FROM resource WHERE code = 'RESOURCE'), 'UPDATE', 'system'),
       ((SELECT id FROM resource WHERE code = 'RESOURCE'), 'DELETE', 'system'),
       ((SELECT id FROM resource WHERE code = 'CURRENCY'), 'READ', 'system'),
       ((SELECT id FROM resource WHERE code = 'CURRENCY'), 'CREATE', 'system'),
       ((SELECT id FROM resource WHERE code = 'CURRENCY'), 'UPDATE', 'system'),
       ((SELECT id FROM resource WHERE code = 'CURRENCY'), 'DELETE', 'system'),
       ((SELECT id FROM resource WHERE code = 'UNIT'), 'READ', 'system'),
       ((SELECT id FROM resource WHERE code = 'UNIT'), 'CREATE', 'system'),
       ((SELECT id FROM resource WHERE code = 'UNIT'), 'UPDATE', 'system'),
       ((SELECT id FROM resource WHERE code = 'UNIT'), 'DELETE', 'system'),
       ((SELECT id FROM resource WHERE code = 'UNIT_DIMENSION'), 'READ', 'system'),
       ((SELECT id FROM resource WHERE code = 'UNIT_DIMENSION'), 'CREATE', 'system'),
       ((SELECT id FROM resource WHERE code = 'UNIT_DIMENSION'), 'UPDATE', 'system'),
       ((SELECT id FROM resource WHERE code = 'UNIT_DIMENSION'), 'DELETE', 'system'),
       ((SELECT id FROM resource WHERE code = 'COUNTRY'), 'READ', 'system'),
       ((SELECT id FROM resource WHERE code = 'COUNTRY'), 'CREATE', 'system'),
       ((SELECT id FROM resource WHERE code = 'COUNTRY'), 'UPDATE', 'system'),
       ((SELECT id FROM resource WHERE code = 'COUNTRY'), 'DELETE', 'system'),
       ((SELECT id FROM resource WHERE code = 'BRAND'), 'READ', 'system'),
       ((SELECT id FROM resource WHERE code = 'BRAND'), 'CREATE', 'system'),
       ((SELECT id FROM resource WHERE code = 'BRAND'), 'UPDATE', 'system'),
       ((SELECT id FROM resource WHERE code = 'BRAND'), 'DELETE', 'system'),
       ((SELECT id FROM resource WHERE code = 'PRODUCER'), 'READ', 'system'),
       ((SELECT id FROM resource WHERE code = 'PRODUCER'), 'CREATE', 'system'),
       ((SELECT id FROM resource WHERE code = 'PRODUCER'), 'UPDATE', 'system'),
       ((SELECT id FROM resource WHERE code = 'PRODUCER'), 'DELETE', 'system'),
       ((SELECT id FROM resource WHERE code = 'PRODUCT_TYPE'), 'READ', 'system'),
       ((SELECT id FROM resource WHERE code = 'PRODUCT_TYPE'), 'CREATE', 'system'),
       ((SELECT id FROM resource WHERE code = 'PRODUCT_TYPE'), 'UPDATE', 'system'),
       ((SELECT id FROM resource WHERE code = 'PRODUCT_TYPE'), 'DELETE', 'system'),
       ((SELECT id FROM resource WHERE code = 'PRODUCT_CLASS'), 'READ', 'system'),
       ((SELECT id FROM resource WHERE code = 'PRODUCT_CLASS'), 'CREATE', 'system'),
       ((SELECT id FROM resource WHERE code = 'PRODUCT_CLASS'), 'UPDATE', 'system'),
       ((SELECT id FROM resource WHERE code = 'PRODUCT_CLASS'), 'DELETE', 'system'),
       ((SELECT id FROM resource WHERE code = 'PRODUCT_GROUP'), 'READ', 'system'),
       ((SELECT id FROM resource WHERE code = 'PRODUCT_GROUP'), 'CREATE', 'system'),
       ((SELECT id FROM resource WHERE code = 'PRODUCT_GROUP'), 'UPDATE', 'system'),
       ((SELECT id FROM resource WHERE code = 'PRODUCT_GROUP'), 'DELETE', 'system'),
       ((SELECT id FROM resource WHERE code = 'PRODUCT_CATEGORY'), 'READ', 'system'),
       ((SELECT id FROM resource WHERE code = 'PRODUCT_CATEGORY'), 'CREATE', 'system'),
       ((SELECT id FROM resource WHERE code = 'PRODUCT_CATEGORY'), 'UPDATE', 'system'),
       ((SELECT id FROM resource WHERE code = 'PRODUCT_CATEGORY'), 'DELETE', 'system'),
       ((SELECT id FROM resource WHERE code = 'PRICE'), 'READ', 'system'),
       ((SELECT id FROM resource WHERE code = 'PRICE'), 'CREATE', 'system'),
       ((SELECT id FROM resource WHERE code = 'PRICE'), 'UPDATE', 'system'),
       ((SELECT id FROM resource WHERE code = 'PRICE'), 'DELETE', 'system'),
       ((SELECT id FROM resource WHERE code = 'WAREHOUSE'), 'READ', 'system'),
       ((SELECT id FROM resource WHERE code = 'WAREHOUSE'), 'CREATE', 'system'),
       ((SELECT id FROM resource WHERE code = 'WAREHOUSE'), 'UPDATE', 'system'),
       ((SELECT id FROM resource WHERE code = 'WAREHOUSE'), 'DELETE', 'system'),
       ((SELECT id FROM resource WHERE code = 'PRODUCT'), 'READ', 'system'),
       ((SELECT id FROM resource WHERE code = 'PRODUCT'), 'CREATE', 'system'),
       ((SELECT id FROM resource WHERE code = 'PRODUCT'), 'UPDATE', 'system'),
       ((SELECT id FROM resource WHERE code = 'PRODUCT'), 'DELETE', 'system'),
       ((SELECT id FROM resource WHERE code = 'PRODUCT_PRICE'), 'READ', 'system'),
       ((SELECT id FROM resource WHERE code = 'PRODUCT_PRICE'), 'CREATE', 'system'),
       ((SELECT id FROM resource WHERE code = 'PRODUCT_PRICE'), 'UPDATE', 'system'),
       ((SELECT id FROM resource WHERE code = 'PRODUCT_PRICE'), 'DELETE', 'system'),
       ((SELECT id FROM resource WHERE code = 'PRODUCT_BARCODE'), 'READ', 'system'),
       ((SELECT id FROM resource WHERE code = 'PRODUCT_BARCODE'), 'CREATE', 'system'),
       ((SELECT id FROM resource WHERE code = 'PRODUCT_BARCODE'), 'UPDATE', 'system'),
       ((SELECT id FROM resource WHERE code = 'PRODUCT_BARCODE'), 'DELETE', 'system'),
       ((SELECT id FROM resource WHERE code = 'PRODUCT_UNIT'), 'READ', 'system'),
       ((SELECT id FROM resource WHERE code = 'PRODUCT_UNIT'), 'CREATE', 'system'),
       ((SELECT id FROM resource WHERE code = 'PRODUCT_UNIT'), 'UPDATE', 'system'),
       ((SELECT id FROM resource WHERE code = 'PRODUCT_UNIT'), 'DELETE', 'system'),
       ((SELECT id FROM resource WHERE code = 'PRODUCT_WAREHOUSE'), 'READ', 'system'),
       ((SELECT id FROM resource WHERE code = 'PRODUCT_WAREHOUSE'), 'CREATE', 'system'),
       ((SELECT id FROM resource WHERE code = 'PRODUCT_WAREHOUSE'), 'UPDATE', 'system'),
       ((SELECT id FROM resource WHERE code = 'PRODUCT_WAREHOUSE'), 'DELETE', 'system'),
       ((SELECT id FROM resource WHERE code = 'TRANSACTION_TYPE'), 'READ', 'system'),
       ((SELECT id FROM resource WHERE code = 'TRANSACTION_TYPE'), 'CREATE', 'system'),
       ((SELECT id FROM resource WHERE code = 'TRANSACTION_TYPE'), 'UPDATE', 'system'),
       ((SELECT id FROM resource WHERE code = 'TRANSACTION_TYPE'), 'DELETE', 'system'),
       ((SELECT id FROM resource WHERE code = 'ACCOUNT'), 'READ', 'system'),
       ((SELECT id FROM resource WHERE code = 'ACCOUNT'), 'CREATE', 'system'),
       ((SELECT id FROM resource WHERE code = 'ACCOUNT'), 'UPDATE', 'system'),
       ((SELECT id FROM resource WHERE code = 'ACCOUNT'), 'DELETE', 'system'),
       ((SELECT id FROM resource WHERE code = 'BUSINESS_PARTNER'), 'READ', 'system'),
       ((SELECT id FROM resource WHERE code = 'BUSINESS_PARTNER'), 'CREATE', 'system'),
       ((SELECT id FROM resource WHERE code = 'BUSINESS_PARTNER'), 'UPDATE', 'system'),
       ((SELECT id FROM resource WHERE code = 'BUSINESS_PARTNER'), 'DELETE', 'system'),
       ((SELECT id FROM resource WHERE code = 'POSTING_RULE'), 'READ', 'system'),
       ((SELECT id FROM resource WHERE code = 'POSTING_RULE'), 'CREATE', 'system'),
       ((SELECT id FROM resource WHERE code = 'POSTING_RULE'), 'UPDATE', 'system'),
       ((SELECT id FROM resource WHERE code = 'POSTING_RULE'), 'DELETE', 'system'),
       ((SELECT id FROM resource WHERE code = 'BUSINESS_PARTNER_ROLE'), 'READ', 'system'),
       ((SELECT id FROM resource WHERE code = 'BUSINESS_PARTNER_ROLE'), 'CREATE', 'system'),
       ((SELECT id FROM resource WHERE code = 'BUSINESS_PARTNER_ROLE'), 'UPDATE', 'system'),
       ((SELECT id FROM resource WHERE code = 'BUSINESS_PARTNER_ROLE'), 'DELETE', 'system'),
       ((SELECT id FROM resource WHERE code = 'STOCK_BALANCE'), 'READ', 'system');

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

INSERT INTO transaction_type(code, name, commercial_affected, accounting_affected, credit_limit_checked, approval_required, stock_affect_direction, created_by)
VALUES ('PRODUCT_APPROVE', 'Product approve', false, true, false, false, 'IN', 'system'),
       ('SALES_ORDER', 'Sales order', true, true, true, true, 'OUT', 'system');

INSERT INTO posting_rule(sequence, type_id, debit_account_id, credit_account_id, amount_source, partner_side)
VALUES (10,
        (SELECT id FROM transaction_type WHERE code = 'SALES_ORDER'),
        (SELECT id FROM account WHERE code = '2100'),
        (SELECT id FROM account WHERE code = '3000'),
        'TOTAL',
        'DEBIT');

INSERT INTO numbering_policy(type_id, prefix, reset_period, sequence_length)
VALUES ((SELECT id FROM transaction_type WHERE code = 'PRODUCT_APPROVE'), 'PA', 'MONTHLY', 6),
       ((SELECT id FROM transaction_type WHERE code = 'SALES_ORDER'), 'SO', 'MONTHLY', 6);