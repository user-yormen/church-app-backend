CREATE TABLE IF NOT EXISTS app_roles (
    id UUID PRIMARY KEY,
    church_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    parent_role_id UUID,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_app_roles_church_name UNIQUE (church_id, name),
    CONSTRAINT fk_app_roles_church FOREIGN KEY (church_id) REFERENCES churches (id),
    CONSTRAINT fk_app_roles_parent FOREIGN KEY (parent_role_id) REFERENCES app_roles (id)
);

CREATE TABLE IF NOT EXISTS app_role_permissions (
    role_id UUID NOT NULL,
    permission_id UUID NOT NULL,
    CONSTRAINT pk_app_role_permissions PRIMARY KEY (role_id, permission_id),
    CONSTRAINT fk_app_role_permissions_role FOREIGN KEY (role_id) REFERENCES app_roles (id),
    CONSTRAINT fk_app_role_permissions_permission FOREIGN KEY (permission_id) REFERENCES permissions (id)
);

CREATE TABLE IF NOT EXISTS app_users (
    id UUID PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    person_id UUID NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_app_users_person FOREIGN KEY (person_id) REFERENCES persons (id)
);

CREATE TABLE IF NOT EXISTS app_user_roles (
    user_id UUID NOT NULL,
    role_id UUID NOT NULL,
    CONSTRAINT pk_app_user_roles PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_app_user_roles_user FOREIGN KEY (user_id) REFERENCES app_users (id),
    CONSTRAINT fk_app_user_roles_role FOREIGN KEY (role_id) REFERENCES app_roles (id)
);

CREATE TABLE IF NOT EXISTS refresh_tokens (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    token_hash VARCHAR(128) NOT NULL UNIQUE,
    expires_at TIMESTAMP NOT NULL,
    revoked_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_refresh_tokens_user FOREIGN KEY (user_id) REFERENCES app_users (id)
);

CREATE INDEX IF NOT EXISTS idx_refresh_token_user ON refresh_tokens (user_id);
