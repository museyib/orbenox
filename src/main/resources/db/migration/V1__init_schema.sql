CREATE TABLE app_user (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(200) NOT NULL,
    display_name VARCHAR(200),
    user_type VARCHAR(100),
    root boolean,
    created_at TIMESTAMP DEFAULT now(),
    updated_at TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

CREATE TABLE app_role (
    id BIGSERIAL PRIMARY KEY,
    role_name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT now(),
    updated_at TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

CREATE TABLE app_user_role (
    app_user_id BIGINT NOT NULL REFERENCES app_user(id),
    app_role_id BIGINT NOT NULL REFERENCES app_role(id),
    PRIMARY KEY (app_user_id, app_role_id)
);

CREATE TABLE resource (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(100) UNIQUE NOT NULL,
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT now(),
    updated_at TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

CREATE TABLE action (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(100) UNIQUE NOT NULL,
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT now(),
    updated_at TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

CREATE TABLE app_permission (
    id BIGSERIAL PRIMARY KEY,
    resource_id BIGINT REFERENCES resource(id),
    action_id BIGINT REFERENCES action(id),
    app_user_id BIGINT REFERENCES app_user(id),
    app_role_id BIGINT REFERENCES app_role(id),
    created_at TIMESTAMP DEFAULT now(),
    updated_at TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

create table resource_action (
    id BIGSERIAL PRIMARY KEY,
    resource_id BIGINT REFERENCES resource(id),
    action_id BIGINT REFERENCES action(id),
    UNIQUE(resource_id, action_id),
    created_at TIMESTAMP DEFAULT now(),
    updated_at TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);