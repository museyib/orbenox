CREATE TABLE inbox_event
(
    id            BIGSERIAL PRIMARY KEY,
    consumer_name VARCHAR(100) NOT NULL,
    event_id      BIGINT       NOT NULL,
    processed_at  TIMESTAMP DEFAULT now(),
    CONSTRAINT uk_inbox_consumer_event
        UNIQUE (consumer_name, event_id)
);
