CREATE TABLE IF NOT EXISTS membership_status_history (
    id UUID PRIMARY KEY,
    membership_id UUID NOT NULL,
    status VARCHAR(32) NOT NULL,
    reason TEXT,
    changed_by_person_id UUID,
    effective_date TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_membership_history_membership FOREIGN KEY (membership_id) REFERENCES church_memberships (id),
    CONSTRAINT fk_membership_history_person FOREIGN KEY (changed_by_person_id) REFERENCES persons (id)
);

CREATE INDEX IF NOT EXISTS idx_membership_history_membership ON membership_status_history (membership_id);
