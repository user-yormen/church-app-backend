CREATE TABLE IF NOT EXISTS church_memberships (
    id UUID PRIMARY KEY,
    person_id UUID NOT NULL,
    church_id UUID NOT NULL,
    join_date TIMESTAMP NOT NULL,
    leave_date TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_church_memberships_person FOREIGN KEY (person_id) REFERENCES persons (id),
    CONSTRAINT fk_church_memberships_church FOREIGN KEY (church_id) REFERENCES churches (id)
);
