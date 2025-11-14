INSERT INTO action (code, name, created_by)
VALUES
    ('READ', 'Action for read permission', 'system'),
    ('CREATE', 'Action for object editing permission', 'system'),
    ('UPDATE', 'Action for object editing permission', 'system'),
    ('DELETE', 'Action for object deleting permission', 'system'),
    ('APPROVE', 'Action for order approve', 'system'),
    ('REJECT', 'Action for order reject', 'system'),
    ('CLOSE', 'Action for order close', 'system');

INSERT INTO unit_dimension (code, name, created_by)
VALUES
    ('COUNT', 'Say', 'system'),
    ('MASS', 'Kütlə', 'system'),
    ('LENGTH', 'Uzunluq', 'system'),
    ('VOLUME', 'Həcm', 'system'),
    ('TEMPERATURE', 'Temperatur', 'system');

INSERT INTO unit (code, unit_dimension_id, is_base, factor_to_base, offset_to_base, name, created_by)
VALUES
    ('PCS', 1, TRUE, 1, 0, 'Ədəd', 'system'),
    ('KG', 2, FALSE, 1000, 0, 'Kiloqram', 'system'),
    ('G', 2, TRUE, 1, 0, 'Qram', 'system'),
    ('M', 3, TRUE, 1, 0, 'Metr', 'system'),
    ('L', 4, TRUE, 1, 0, 'Litr', 'system'),
    ('M3', 4, FALSE, 1000, 0, 'Litr', 'system'),
    ('C', 5, TRUE, 1, 0, 'Selsi', 'system'),
    ('K', 5, FALSE, 1, -273.15, 'Kelvin', 'system');