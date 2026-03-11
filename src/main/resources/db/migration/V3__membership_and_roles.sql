ALTER TABLE persons
    ADD COLUMN IF NOT EXISTS marital_status VARCHAR(32);

ALTER TABLE church_memberships ADD COLUMN IF NOT EXISTS status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE';
ALTER TABLE church_memberships ADD COLUMN IF NOT EXISTS type VARCHAR(32) NOT NULL DEFAULT 'COMMUNICANT';
ALTER TABLE church_memberships ADD COLUMN IF NOT EXISTS join_method VARCHAR(255);

ALTER TABLE person_app_roles
    ADD COLUMN IF NOT EXISTS scope_type VARCHAR(32);
ALTER TABLE person_app_roles
    ADD COLUMN IF NOT EXISTS scope_id UUID;
ALTER TABLE person_app_roles
    ADD COLUMN IF NOT EXISTS source VARCHAR(32) NOT NULL DEFAULT 'APPOINTMENT';
ALTER TABLE person_app_roles
    ADD COLUMN IF NOT EXISTS status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE';

UPDATE person_app_roles
SET scope_type = 'CHURCH',
    scope_id = (SELECT church_id FROM app_roles WHERE app_roles.id = person_app_roles.app_role_id)
WHERE scope_type IS NULL OR scope_id IS NULL;

ALTER TABLE person_app_roles
    ALTER COLUMN scope_type SET NOT NULL;
ALTER TABLE person_app_roles
    ALTER COLUMN scope_id SET NOT NULL;
