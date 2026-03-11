CREATE TABLE IF NOT EXISTS person_app_roles (
    id UUID PRIMARY KEY,
    person_id UUID NOT NULL,
    app_role_id UUID NOT NULL,
    assigned_date TIMESTAMP NOT NULL,
    expiry_date TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_person_app_roles_person FOREIGN KEY (person_id) REFERENCES persons (id),
    CONSTRAINT fk_person_app_roles_role FOREIGN KEY (app_role_id) REFERENCES app_roles (id)
);
