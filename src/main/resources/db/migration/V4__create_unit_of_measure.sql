CREATE TABLE unit_of_measure (
     id BIGSERIAL PRIMARY KEY,
     code VARCHAR(100) UNIQUE NOT NULL,
     description VARCHAR(255),
     created_at TIMESTAMP DEFAULT now(),
     updated_at TIMESTAMP,
     created_by VARCHAR(100),
     updated_by VARCHAR(100)
);

INSERT INTO unit_of_measure (code, description, created_by)
VALUES
    ('əd', 'Ədəd', 'system'),
    ('kq', 'Kiloqram', 'system'),
    ('q', 'Qram', 'system'),
    ('m', 'Metr', 'system'),
    ('l', 'Litr', 'system')