CREATE TABLE user_type
(
    id         BIGSERIAL PRIMARY KEY,
    code       VARCHAR(100) NOT NULL,
    name       VARCHAR(255) NOT NULL,
    created_at TIMESTAMP             DEFAULT now(),
    updated_at TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    enabled    BOOLEAN      NOT NULL DEFAULT TRUE,
    deleted    BOOLEAN      NOT NULL DEFAULT FALSE
);
CREATE UNIQUE INDEX ux_user_type_code_active
    ON user_type (code)
    WHERE deleted = false;

CREATE TABLE app_user
(
    id           BIGSERIAL PRIMARY KEY,
    username     VARCHAR(100) NOT NULL,
    password     VARCHAR(200) NOT NULL,
    display_name VARCHAR(200),
    user_type_id BIGINT       NOT NULL REFERENCES user_type (id),
    language     VARCHAR(10),
    root         boolean,
    created_at   TIMESTAMP             DEFAULT now(),
    updated_at   TIMESTAMP,
    created_by   VARCHAR(100),
    updated_by   VARCHAR(100),
    enabled      BOOLEAN      NOT NULL DEFAULT TRUE,
    deleted      BOOLEAN      NOT NULL DEFAULT FALSE
);
CREATE UNIQUE INDEX ux_app_user_username_active
    ON app_user (username)
    WHERE deleted = false;

CREATE TABLE app_role
(
    id         BIGSERIAL PRIMARY KEY,
    code       VARCHAR(100) NOT NULL,
    name       VARCHAR(100) NOT NULL,
    created_at TIMESTAMP             DEFAULT now(),
    updated_at TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    enabled    BOOLEAN      NOT NULL DEFAULT TRUE,
    deleted    BOOLEAN      NOT NULL DEFAULT FALSE
);
CREATE UNIQUE INDEX ux_app_role_code_active
    ON app_role (code)
    WHERE deleted = false;

CREATE TABLE app_user_role
(
    id          BIGSERIAL PRIMARY KEY,
    app_user_id BIGINT NOT NULL REFERENCES app_user (id),
    app_role_id BIGINT NOT NULL REFERENCES app_role (id),
    created_at  TIMESTAMP DEFAULT now(),
    updated_at  TIMESTAMP,
    created_by  VARCHAR(100),
    updated_by  VARCHAR(100),
    UNIQUE (app_user_id, app_role_id)
);

CREATE TABLE resource
(
    id         BIGSERIAL PRIMARY KEY,
    code       VARCHAR(100) NOT NULL,
    name       VARCHAR(255) NOT NULL,
    created_at TIMESTAMP             DEFAULT now(),
    updated_at TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    enabled    BOOLEAN      NOT NULL DEFAULT TRUE,
    deleted    BOOLEAN      NOT NULL DEFAULT FALSE
);
CREATE UNIQUE INDEX ux_resource_code_active
    ON resource (code)
    WHERE deleted = false;

CREATE TABLE app_permission
(
    id          BIGSERIAL PRIMARY KEY,
    resource_id BIGINT REFERENCES resource (id),
    action      VARCHAR(100) NOT NULL,
    app_user_id BIGINT REFERENCES app_user (id),
    app_role_id BIGINT REFERENCES app_role (id),
    created_at  TIMESTAMP             DEFAULT now(),
    updated_at  TIMESTAMP,
    created_by  VARCHAR(100),
    updated_by  VARCHAR(100),
    enabled     BOOLEAN      NOT NULL DEFAULT TRUE,
    deleted     BOOLEAN      NOT NULL DEFAULT FALSE
);

CREATE TABLE resource_action
(
    id          BIGSERIAL PRIMARY KEY,
    resource_id BIGINT REFERENCES resource (id),
    action      VARCHAR(100) NOT NULL,
    UNIQUE (resource_id, action),
    created_at  TIMESTAMP             DEFAULT now(),
    updated_at  TIMESTAMP,
    created_by  VARCHAR(100),
    updated_by  VARCHAR(100),
    enabled     BOOLEAN      NOT NULL DEFAULT TRUE,
    deleted     BOOLEAN      NOT NULL DEFAULT FALSE
);

CREATE TABLE unit_dimension
(
    id         BIGSERIAL PRIMARY KEY,
    code       VARCHAR(100) NOT NULL,
    name       VARCHAR(255) NOT NULL,
    created_at TIMESTAMP             DEFAULT now(),
    updated_at TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    enabled    BOOLEAN      NOT NULL DEFAULT TRUE,
    deleted    BOOLEAN      NOT NULL DEFAULT FALSE
);
CREATE UNIQUE INDEX ux_unit_dimension_code_active
    ON unit_dimension (code)
    WHERE deleted = false;

CREATE TABLE unit
(
    id                BIGSERIAL PRIMARY KEY,
    unit_dimension_id BIGINT          NOT NULL REFERENCES unit_dimension (id),
    code              VARCHAR(100)    NOT NULL,
    name              VARCHAR(255),
    base              BOOLEAN         NOT NULL DEFAULT FALSE,
    factor_to_base    NUMERIC(20, 10) NOT NULL DEFAULT 1,
    offset_to_base    NUMERIC(20, 10) NOT NULL DEFAULT 0,
    created_at        TIMESTAMP                DEFAULT now(),
    updated_at        TIMESTAMP,
    created_by        VARCHAR(100),
    updated_by        VARCHAR(100),
    enabled           BOOLEAN         NOT NULL DEFAULT TRUE,
    deleted           BOOLEAN         NOT NULL DEFAULT FALSE
);
CREATE UNIQUE INDEX ux_unit_dimension_base
    ON unit (unit_dimension_id)
    WHERE base = true;
CREATE UNIQUE INDEX ux_unit_code_active
    ON unit (code)
    WHERE deleted = false;

CREATE TABLE currency
(
    id         BIGSERIAL PRIMARY KEY,
    code       VARCHAR(100) NOT NULL,
    name       VARCHAR(255) NOT NULL,
    created_at TIMESTAMP             DEFAULT now(),
    updated_at TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    enabled    BOOLEAN      NOT NULL DEFAULT TRUE,
    deleted    BOOLEAN      NOT NULL DEFAULT FALSE
);
CREATE UNIQUE INDEX ux_currency_code_active
    ON currency (code)
    WHERE deleted = false;

CREATE TABLE country
(
    id         BIGSERIAL PRIMARY KEY,
    code       VARCHAR(100) NOT NULL,
    name       VARCHAR(255) NOT NULL,
    created_at TIMESTAMP             DEFAULT now(),
    updated_at TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    enabled    BOOLEAN      NOT NULL DEFAULT TRUE,
    deleted    BOOLEAN      NOT NULL DEFAULT FALSE
);
CREATE UNIQUE INDEX ux_country_code_active
    ON country (code)
    WHERE deleted = false;

CREATE TABLE brand
(
    id          BIGSERIAL PRIMARY KEY,
    code        VARCHAR(100) NOT NULL,
    name        VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    logo_url    VARCHAR(200),
    created_at  TIMESTAMP             DEFAULT now(),
    updated_at  TIMESTAMP,
    created_by  VARCHAR(100),
    updated_by  VARCHAR(100),
    enabled     BOOLEAN      NOT NULL DEFAULT TRUE,
    deleted     BOOLEAN      NOT NULL DEFAULT FALSE
);
CREATE UNIQUE INDEX ux_brand_code_active
    ON brand (code)
    WHERE deleted = false;

CREATE TABLE producer
(
    id          BIGSERIAL PRIMARY KEY,
    code        VARCHAR(100) NOT NULL,
    name        VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    created_at  TIMESTAMP             DEFAULT now(),
    updated_at  TIMESTAMP,
    created_by  VARCHAR(100),
    updated_by  VARCHAR(100),
    enabled     BOOLEAN      NOT NULL DEFAULT TRUE,
    deleted     BOOLEAN      NOT NULL DEFAULT FALSE
);
CREATE UNIQUE INDEX ux_producer_code_active
    ON producer (code)
    WHERE deleted = false;

CREATE TABLE product_type
(
    id          BIGSERIAL PRIMARY KEY,
    code        VARCHAR(100) NOT NULL,
    name        VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    created_at  TIMESTAMP             DEFAULT now(),
    updated_at  TIMESTAMP,
    created_by  VARCHAR(100),
    updated_by  VARCHAR(100),
    enabled     BOOLEAN      NOT NULL DEFAULT TRUE,
    deleted     BOOLEAN      NOT NULL DEFAULT FALSE
);
CREATE UNIQUE INDEX ux_product_type_code_active
    ON product_type (code)
    WHERE deleted = false;

CREATE TABLE product_group
(
    id          BIGSERIAL PRIMARY KEY,
    code        VARCHAR(100) NOT NULL,
    name        VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    parent_id   BIGINT REFERENCES product_group (id),
    slug        VARCHAR(100),
    created_at  TIMESTAMP             DEFAULT now(),
    updated_at  TIMESTAMP,
    created_by  VARCHAR(100),
    updated_by  VARCHAR(100),
    enabled     BOOLEAN      NOT NULL DEFAULT TRUE,
    deleted     BOOLEAN      NOT NULL DEFAULT FALSE
);
CREATE UNIQUE INDEX ux_product_group_code_active
    ON product_group (code)
    WHERE deleted = false;

CREATE TABLE product_category
(
    id          BIGSERIAL PRIMARY KEY,
    code        VARCHAR(100) NOT NULL,
    name        VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    created_at  TIMESTAMP             DEFAULT now(),
    updated_at  TIMESTAMP,
    created_by  VARCHAR(100),
    updated_by  VARCHAR(100),
    enabled     BOOLEAN      NOT NULL DEFAULT TRUE,
    deleted     BOOLEAN      NOT NULL DEFAULT FALSE
);
CREATE UNIQUE INDEX ux_product_category_code_active
    ON product_category (code)
    WHERE deleted = false;

CREATE TABLE product_class
(
    id          BIGSERIAL PRIMARY KEY,
    code        VARCHAR(100) NOT NULL,
    name        VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    created_at  TIMESTAMP             DEFAULT now(),
    updated_at  TIMESTAMP,
    created_by  VARCHAR(100),
    updated_by  VARCHAR(100),
    enabled     BOOLEAN      NOT NULL DEFAULT TRUE,
    deleted     BOOLEAN      NOT NULL DEFAULT FALSE
);
CREATE UNIQUE INDEX ux_product_class_code_active
    ON product_class (code)
    WHERE deleted = false;

CREATE TABLE product
(
    id                  BIGSERIAL PRIMARY KEY,
    code                VARCHAR(100)       NOT NULL,
    name                VARCHAR(255),
    description         VARCHAR(2000),
    product_type_id     BIGINT REFERENCES product_type (id),
    brand_id            BIGINT REFERENCES brand (id),
    producer_id         BIGINT REFERENCES producer (id),
    product_group_id    BIGINT REFERENCES product_group (id),
    product_category_id BIGINT REFERENCES product_category (id),
    product_class_id    BIGINT REFERENCES product_class (id),
    country_id          BIGINT REFERENCES country (id),
    default_unit_id     BIGINT REFERENCES unit (id),
    default_barcode     VARCHAR(50) UNIQUE NOT NULL,
    created_at          TIMESTAMP                   DEFAULT now(),
    updated_at          TIMESTAMP,
    created_by          VARCHAR(100),
    updated_by          VARCHAR(100),
    enabled             BOOLEAN            NOT NULL DEFAULT TRUE,
    deleted             BOOLEAN            NOT NULL DEFAULT FALSE
);
CREATE UNIQUE INDEX ux_product_code_active
    ON product (code)
    WHERE deleted = false;

CREATE TABLE price_list
(
    id               BIGSERIAL PRIMARY KEY,
    code             VARCHAR(100)    NOT NULL,
    name             VARCHAR(255),
    currency_id      BIGINT          NOT NULL REFERENCES currency (id),
    parent_id        BIGINT REFERENCES price_list (id),
    factor_to_parent NUMERIC(20, 10) NOT NULL DEFAULT 1,
    round_length     INT                      DEFAULT 4,
    created_at       TIMESTAMP                DEFAULT now(),
    updated_at       TIMESTAMP,
    created_by       VARCHAR(100),
    updated_by       VARCHAR(100),
    enabled          BOOLEAN         NOT NULL DEFAULT TRUE,
    deleted          BOOLEAN         NOT NULL DEFAULT FALSE
);
CREATE UNIQUE INDEX ux_price_list_code_active
    ON price_list (code)
    WHERE deleted = false;

CREATE TABLE warehouse
(
    id         BIGSERIAL PRIMARY KEY,
    code       VARCHAR(100) NOT NULL,
    name       VARCHAR(255) NOT NULL,
    created_at TIMESTAMP             DEFAULT now(),
    updated_at TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    enabled    BOOLEAN      NOT NULL DEFAULT TRUE,
    deleted    BOOLEAN      NOT NULL DEFAULT FALSE
);
CREATE UNIQUE INDEX ux_warehouse_code_active
    ON warehouse (code)
    WHERE deleted = false;

CREATE TABLE account
(
    id           BIGSERIAL PRIMARY KEY,
    code         VARCHAR(100) NOT NULL,
    name         VARCHAR(255) NOT NULL,
    account_type VARCHAR(20)  NOT NULL,
    created_at   TIMESTAMP             DEFAULT now(),
    updated_at   TIMESTAMP,
    created_by   VARCHAR(100),
    updated_by   VARCHAR(100),
    enabled      BOOLEAN      NOT NULL DEFAULT TRUE,
    deleted      BOOLEAN      NOT NULL DEFAULT FALSE
);
CREATE UNIQUE INDEX ux_account_code_active
    ON account (code)
    WHERE deleted = false;

CREATE TABLE business_partner
(
    id         BIGSERIAL PRIMARY KEY,
    code       VARCHAR(100) NOT NULL,
    name       VARCHAR(255) NOT NULL,
    type       VARCHAR(20)  NOT NULL,
    tax_id     VARCHAR(20),
    created_at TIMESTAMP             DEFAULT now(),
    updated_at TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    enabled    BOOLEAN      NOT NULL DEFAULT TRUE,
    deleted    BOOLEAN      NOT NULL DEFAULT FALSE
);
CREATE UNIQUE INDEX ux_business_partner_code_active
    ON business_partner (code)
    WHERE deleted = false;

CREATE TABLE business_partner_role
(
    id         BIGSERIAL PRIMARY KEY,
    partner_id BIGINT  NOT NULL REFERENCES business_partner (id),
    role       VARCHAR(20),
    created_at TIMESTAMP        DEFAULT now(),
    updated_at TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    enabled    BOOLEAN NOT NULL DEFAULT TRUE,
    deleted    BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE product_price
(
    id                   BIGSERIAL PRIMARY KEY,
    product_id           BIGINT          NOT NULL REFERENCES product (id),
    price_list_id        BIGINT          NOT NULL REFERENCES price_list (id),
    unit_id              BIGINT          NOT NULL REFERENCES unit (id),
    price                NUMERIC(20, 10)          DEFAULT 0,
    factor_to_parent     NUMERIC(20, 10) NOT NULL DEFAULT 1,
    fixed_price          BOOLEAN                  DEFAULT FALSE,
    round_length         INT                      DEFAULT 4,
    discount_ratio_limit NUMERIC(20, 10)          DEFAULT 0,
    created_at           TIMESTAMP                DEFAULT now(),
    updated_at           TIMESTAMP,
    created_by           VARCHAR(100),
    updated_by           VARCHAR(100),
    UNIQUE (product_id, price_list_id, unit_id)
);

CREATE TABLE product_barcode
(
    id         BIGSERIAL PRIMARY KEY,
    product_id BIGINT             NOT NULL REFERENCES product (id),
    unit_id    BIGINT             NOT NULL REFERENCES unit (id),
    barcode    VARCHAR(50) UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT now(),
    updated_at TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

CREATE TABLE product_unit
(
    id             BIGSERIAL PRIMARY KEY,
    product_id     BIGINT          NOT NULL REFERENCES product (id),
    unit_id        BIGINT          NOT NULL REFERENCES unit (id),
    factor_to_base NUMERIC(20, 10) NOT NULL DEFAULT 1,
    created_at     TIMESTAMP                DEFAULT now(),
    updated_at     TIMESTAMP,
    created_by     VARCHAR(100),
    updated_by     VARCHAR(100),
    UNIQUE (product_id, unit_id)
);

CREATE TABLE product_warehouse
(
    id           BIGSERIAL PRIMARY KEY,
    product_id   BIGINT          NOT NULL REFERENCES product (id),
    warehouse_id BIGINT          NOT NULL REFERENCES warehouse (id),
    min_quantity NUMERIC(20, 10) NOT NULL DEFAULT 0,
    max_quantity NUMERIC(20, 10) NOT NULL DEFAULT 999999999,
    created_at   TIMESTAMP                DEFAULT now(),
    updated_at   TIMESTAMP,
    created_by   VARCHAR(100),
    updated_by   VARCHAR(100),
    UNIQUE (product_id, warehouse_id)
);

CREATE TABLE transaction_type
(
    id                     BIGSERIAL PRIMARY KEY,
    code                   VARCHAR(100) NOT NULL,
    name                   VARCHAR(255) NOT NULL,
    document_no_prefix          VARCHAR(5) NOT NULL,
    commercial_affected    BOOLEAN      NOT NULL DEFAULT FALSE,
    accounting_affected    BOOLEAN      NOT NULL DEFAULT FALSE,
    credit_limit_checked   BOOLEAN      NOT NULL DEFAULT FALSE,
    approval_required      BOOLEAN      NOT NULL DEFAULT FALSE,
    stock_affect_direction VARCHAR(10),
    created_at             TIMESTAMP             DEFAULT now(),
    updated_at             TIMESTAMP,
    created_by             VARCHAR(100),
    updated_by             VARCHAR(100),
    enabled                BOOLEAN      NOT NULL DEFAULT TRUE,
    deleted                BOOLEAN      NOT NULL DEFAULT FALSE
);
CREATE UNIQUE INDEX ux_transaction_type_code_active
    ON transaction_type (code)
    WHERE deleted = false;

CREATE TABLE posting_rule
(
    id                BIGSERIAL PRIMARY KEY,
    sequence          INT NOT NULL,
    type_id           BIGINT REFERENCES transaction_type (id),
    debit_account_id  BIGINT REFERENCES account (id),
    credit_account_id BIGINT REFERENCES account (id),
    amount_source     VARCHAR(20),
    partner_side      VARCHAR(20),
    UNIQUE (type_id, sequence)
);

CREATE TABLE document
(
    id              BIGSERIAL PRIMARY KEY,
    document_no          VARCHAR(100) NOT NULL,
    document_date   DATE         NOT NULL,
    type_id         BIGINT REFERENCES transaction_type (id),
    description     VARCHAR(1000),
    document_status VARCHAR(100),
    approval_status VARCHAR(100),
    owner_id        BIGINT REFERENCES app_user (id),
    created_at      TIMESTAMP             DEFAULT now(),
    updated_at      TIMESTAMP,
    created_by      VARCHAR(100),
    updated_by      VARCHAR(100),
    enabled         BOOLEAN      NOT NULL DEFAULT TRUE,
    deleted         BOOLEAN      NOT NULL DEFAULT FALSE
);

CREATE TABLE commercial_context
(
    document_id    BIGINT PRIMARY KEY REFERENCES document (id),
    partner_id     BIGINT NOT NULL REFERENCES business_partner (id),
    price_list_id  BIGINT REFERENCES price_list (id),
    currency_id    BIGINT REFERENCES currency (id),
    payment_method VARCHAR(20),
    due_date       DATE   NOT NULL
);

CREATE TABLE journal_entry
(
    id           BIGSERIAL PRIMARY KEY,
    document_id  BIGINT REFERENCES document (id),
    posting_date TIMESTAMP NOT NULL,
    status       VARCHAR(20)
);

CREATE TABLE journal_line
(
    id               BIGSERIAL PRIMARY KEY,
    journal_entry_id BIGINT REFERENCES journal_entry (id),
    account_id       BIGINT REFERENCES account (id),
    partner_id       BIGINT REFERENCES business_partner (id),
    debit            NUMERIC(20, 10) NOT NULL DEFAULT 0,
    credit           NUMERIC(20, 10) NOT NULL DEFAULT 0,
    CHECK (
        (debit > 0 AND credit = 0)
            OR (credit > 0 AND debit = 0)
        )
);

CREATE TABLE product_line
(
    id          BIGSERIAL PRIMARY KEY,
    document_id BIGINT REFERENCES document (id),
    product_id  BIGINT REFERENCES product (id),
    unit_id     BIGINT REFERENCES unit (id),
    quantity    NUMERIC(20, 10) NOT NULL DEFAULT 0,
    unit_price  NUMERIC(20, 10) NOT NULL DEFAULT 0,
    discount    NUMERIC(20, 10) NOT NULL DEFAULT 0
);

CREATE TABLE stock_context
(
    id                  BIGSERIAL PRIMARY KEY,
    document_id         BIGINT REFERENCES document (id),
    source_warehouse_id BIGINT REFERENCES warehouse (id),
    target_warehouse_id BIGINT REFERENCES warehouse (id)
);

CREATE TABLE stock_movement
(
    id           BIGSERIAL PRIMARY KEY,
    document_id  BIGINT REFERENCES document (id),
    product_id   BIGINT REFERENCES product (id),
    warehouse_id BIGINT REFERENCES warehouse (id),
    quantity     NUMERIC(20, 10) NOT NULL DEFAULT 0,
    occurred_at  TIMESTAMP                DEFAULT now()
);

CREATE TABLE stock_balance
(
    id                BIGSERIAL PRIMARY KEY,
    product_id        BIGINT REFERENCES product (id),
    warehouse_id      BIGINT REFERENCES warehouse (id),
    quantity          NUMERIC(20, 10) NOT NULL DEFAULT 0,
    reserved_quantity NUMERIC(20, 10) NOT NULL DEFAULT 0,
    free_quantity     NUMERIC(20, 10) GENERATED ALWAYS AS ( quantity - reserved_quantity ) STORED,
    UNIQUE (product_id, warehouse_id),
    CHECK ( NOT quantity < 0 )
)