CREATE TABLE outbox_event
(
    id                BIGSERIAL PRIMARY KEY,
    event_type        VARCHAR(100) NOT NULL,
    aggregate_type    VARCHAR(255) NOT NULL,
    aggregate_id      VARCHAR(255),
    aggregate_version VARCHAR(255),
    payload           TEXT,
    created_at        TIMESTAMP DEFAULT now(),
    published_at      TIMESTAMP,
    status            VARCHAR(100)
);
