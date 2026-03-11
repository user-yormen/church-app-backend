CREATE TABLE IF NOT EXISTS groups (
    id UUID PRIMARY KEY,
    church_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    type VARCHAR(32) NOT NULL,
    status VARCHAR(32) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_groups_church_name UNIQUE (church_id, name),
    CONSTRAINT fk_groups_church FOREIGN KEY (church_id) REFERENCES churches (id)
);

CREATE TABLE IF NOT EXISTS group_members (
    id UUID PRIMARY KEY,
    group_id UUID NOT NULL,
    person_id UUID NOT NULL,
    status VARCHAR(32) NOT NULL,
    joined_at TIMESTAMP,
    left_at TIMESTAMP,
    dues_status VARCHAR(32) NOT NULL,
    dues_paid_through TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_group_members UNIQUE (group_id, person_id),
    CONSTRAINT fk_group_members_group FOREIGN KEY (group_id) REFERENCES groups (id),
    CONSTRAINT fk_group_members_person FOREIGN KEY (person_id) REFERENCES persons (id)
);

CREATE TABLE IF NOT EXISTS elections (
    id UUID PRIMARY KEY,
    church_id UUID NOT NULL,
    scope_type VARCHAR(32) NOT NULL,
    scope_id UUID NOT NULL,
    role_id UUID NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    nomination_start TIMESTAMP NOT NULL,
    nomination_end TIMESTAMP NOT NULL,
    voting_start TIMESTAMP NOT NULL,
    voting_end TIMESTAMP NOT NULL,
    status VARCHAR(32) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_elections_church FOREIGN KEY (church_id) REFERENCES churches (id),
    CONSTRAINT fk_elections_role FOREIGN KEY (role_id) REFERENCES app_roles (id)
);

CREATE TABLE IF NOT EXISTS election_eligibility_rules (
    id UUID PRIMARY KEY,
    election_id UUID NOT NULL,
    rule_type VARCHAR(32) NOT NULL,
    rule_config TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_election_rules_election FOREIGN KEY (election_id) REFERENCES elections (id)
);

CREATE TABLE IF NOT EXISTS election_candidates (
    id UUID PRIMARY KEY,
    election_id UUID NOT NULL,
    person_id UUID NOT NULL,
    status VARCHAR(32) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_election_candidates UNIQUE (election_id, person_id),
    CONSTRAINT fk_election_candidates_election FOREIGN KEY (election_id) REFERENCES elections (id),
    CONSTRAINT fk_election_candidates_person FOREIGN KEY (person_id) REFERENCES persons (id)
);

CREATE TABLE IF NOT EXISTS election_votes (
    id UUID PRIMARY KEY,
    election_id UUID NOT NULL,
    voter_person_id UUID NOT NULL,
    candidate_id UUID NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_election_votes UNIQUE (election_id, voter_person_id),
    CONSTRAINT fk_election_votes_election FOREIGN KEY (election_id) REFERENCES elections (id),
    CONSTRAINT fk_election_votes_voter FOREIGN KEY (voter_person_id) REFERENCES persons (id),
    CONSTRAINT fk_election_votes_candidate FOREIGN KEY (candidate_id) REFERENCES election_candidates (id)
);

CREATE TABLE IF NOT EXISTS election_results (
    id UUID PRIMARY KEY,
    election_id UUID NOT NULL,
    person_id UUID NOT NULL,
    total_votes INT NOT NULL,
    status VARCHAR(32) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_election_results UNIQUE (election_id, person_id),
    CONSTRAINT fk_election_results_election FOREIGN KEY (election_id) REFERENCES elections (id),
    CONSTRAINT fk_election_results_person FOREIGN KEY (person_id) REFERENCES persons (id)
);

CREATE TABLE IF NOT EXISTS achievements (
    id UUID PRIMARY KEY,
    church_id UUID NOT NULL,
    person_id UUID,
    group_id UUID,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    achieved_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_achievements_church FOREIGN KEY (church_id) REFERENCES churches (id),
    CONSTRAINT fk_achievements_person FOREIGN KEY (person_id) REFERENCES persons (id),
    CONSTRAINT fk_achievements_group FOREIGN KEY (group_id) REFERENCES groups (id)
);

CREATE TABLE IF NOT EXISTS share_grants (
    id UUID PRIMARY KEY,
    resource_type VARCHAR(64) NOT NULL,
    resource_id UUID NOT NULL,
    owner_church_id UUID NOT NULL,
    grantee_church_id UUID NOT NULL,
    access_level VARCHAR(16) NOT NULL,
    created_by_person_id UUID NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_share_grants_owner FOREIGN KEY (owner_church_id) REFERENCES churches (id),
    CONSTRAINT fk_share_grants_grantee FOREIGN KEY (grantee_church_id) REFERENCES churches (id),
    CONSTRAINT fk_share_grants_person FOREIGN KEY (created_by_person_id) REFERENCES persons (id)
);

CREATE INDEX IF NOT EXISTS idx_share_grants_resource ON share_grants (resource_type, resource_id);
