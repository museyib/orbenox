CREATE TABLE stock_movement
(
    id              BIGSERIAL PRIMARY KEY,
    document_id     BIGINT          NOT NULL,
    product_line_id BIGINT          NOT NULL,
    product_id      BIGINT,
    warehouse_id    BIGINT,
    quantity        NUMERIC(20, 10) NOT NULL DEFAULT 0,
    occurred_at     TIMESTAMP                DEFAULT now()
);

CREATE TABLE stock_balance
(
    id                BIGSERIAL PRIMARY KEY,
    product_id        BIGINT          NOT NULL,
    warehouse_id      BIGINT          NOT NULL,
    quantity          NUMERIC(20, 10) NOT NULL DEFAULT 0,
    reserved_quantity NUMERIC(20, 10) NOT NULL DEFAULT 0,
    free_quantity     NUMERIC(20, 10) GENERATED ALWAYS AS ( quantity - reserved_quantity ) STORED,
    UNIQUE (product_id, warehouse_id),
    CHECK ( quantity >= 0 )
);


CREATE TABLE outbox_event
(
    id                BIGSERIAL PRIMARY KEY,
    event_type        VARCHAR(100) NOT NULL,
    aggregate_type    VARCHAR(255) NOT NULL,
    aggregate_id      VARCHAR(255),
    aggregate_version VARCHAR(255),
    payload           TEXT,
    created_at        TIMESTAMP    NOT NULL DEFAULT now(),
    published_at      TIMESTAMP,
    status            VARCHAR(100),
    routing_key        VARCHAR(100)
);

CREATE TABLE inbox_event
(
    id            BIGSERIAL PRIMARY KEY,
    consumer_name VARCHAR(100) NOT NULL,
    event_id      BIGINT       NOT NULL,
    processed_at  TIMESTAMP DEFAULT now(),
    CONSTRAINT uk_inbox_consumer_event
        UNIQUE (consumer_name, event_id)
);

CREATE TABLE shedlock
(
    name VARCHAR(100) PRIMARY KEY ,
    lock_until TIMESTAMP,
    locked_at TIMESTAMP,
    locked_by VARCHAR(100)
)
