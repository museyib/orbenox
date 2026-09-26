-- Migration V4: Create Idempotency table

CREATE TABLE idempotency_record
(
    id              BIGSERIAL PRIMARY KEY,
    idempotency_key VARCHAR(100) NOT NULL,
    status          VARCHAR(255) NOT NULL,
    request_hash    VARCHAR(255),
    response_body   VARCHAR(255),
    response_status INT,
    created_at      TIMESTAMP DEFAULT now(),
    updated_at      TIMESTAMP,
    created_by      VARCHAR(100),
    updated_by      VARCHAR(100),
    UNIQUE (idempotency_key)
)
