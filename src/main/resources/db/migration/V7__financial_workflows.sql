CREATE TABLE IF NOT EXISTS collection_types (
    id UUID PRIMARY KEY,
    church_id UUID NOT NULL REFERENCES churches(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    default_frequency VARCHAR(255),
    expected_counting_method VARCHAR(255),
    accounting_category VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_collection_types_church_name UNIQUE (church_id, name)
);

CREATE TABLE IF NOT EXISTS collection_events (
    id UUID PRIMARY KEY,
    collection_type_id UUID NOT NULL REFERENCES collection_types(id),
    church_id UUID NOT NULL REFERENCES churches(id),
    service_reference VARCHAR(255),
    event_date TIMESTAMP NOT NULL,
    location VARCHAR(255),
    currency VARCHAR(3) NOT NULL,
    status VARCHAR(32) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS counting_sessions (
    id UUID PRIMARY KEY,
    collection_event_id UUID NOT NULL REFERENCES collection_events(id) ON DELETE CASCADE,
    counting_method VARCHAR(32) NOT NULL,
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    status VARCHAR(32) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS counting_participants (
    id UUID PRIMARY KEY,
    counting_session_id UUID NOT NULL REFERENCES counting_sessions(id) ON DELETE CASCADE,
    person_id UUID NOT NULL REFERENCES persons(id),
    role_id UUID NOT NULL REFERENCES app_roles(id),
    participation_type VARCHAR(64) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS collection_amount_breakdowns (
    id UUID PRIMARY KEY,
    counting_session_id UUID NOT NULL REFERENCES counting_sessions(id) ON DELETE CASCADE,
    total_amount NUMERIC(19, 2) NOT NULL,
    notes_amount NUMERIC(19, 2),
    coins_amount NUMERIC(19, 2),
    cheques_amount NUMERIC(19, 2),
    transfers_amount NUMERIC(19, 2),
    denomination_breakdown TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS collection_confirmations (
    id UUID PRIMARY KEY,
    collection_event_id UUID NOT NULL REFERENCES collection_events(id) ON DELETE CASCADE,
    person_id UUID NOT NULL REFERENCES persons(id),
    role_id UUID NOT NULL REFERENCES app_roles(id),
    confirmation_timestamp TIMESTAMP NOT NULL,
    confirmation_level VARCHAR(64),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS financial_audit_logs (
    id BIGSERIAL PRIMARY KEY,
    person_id UUID NOT NULL REFERENCES persons(id),
    role_id UUID NOT NULL REFERENCES app_roles(id),
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    action VARCHAR(255) NOT NULL,
    entity_type VARCHAR(255),
    entity_id UUID,
    previous_value TEXT,
    new_value TEXT
);

INSERT INTO collection_types (
    id,
    church_id,
    name,
    description,
    default_frequency,
    expected_counting_method,
    accounting_category
)
SELECT
    (
        substr(md5(c.id::text || ':OFFERTORY'), 1, 8) || '-' ||
        substr(md5(c.id::text || ':OFFERTORY'), 9, 4) || '-' ||
        substr(md5(c.id::text || ':OFFERTORY'), 13, 4) || '-' ||
        substr(md5(c.id::text || ':OFFERTORY'), 17, 4) || '-' ||
        substr(md5(c.id::text || ':OFFERTORY'), 21, 12)
    )::uuid,
    c.id,
    'OFFERTORY',
    'Regular Sunday offertory collections.',
    'WEEKLY',
    'MANUAL',
    'GENERAL_OFFERING'
FROM churches c
WHERE NOT EXISTS (
    SELECT 1
    FROM collection_types ct
    WHERE ct.church_id = c.id
      AND ct.name = 'OFFERTORY'
);

INSERT INTO collection_types (
    id,
    church_id,
    name,
    description,
    default_frequency,
    expected_counting_method,
    accounting_category
)
SELECT
    (
        substr(md5(c.id::text || ':TITHE'), 1, 8) || '-' ||
        substr(md5(c.id::text || ':TITHE'), 9, 4) || '-' ||
        substr(md5(c.id::text || ':TITHE'), 13, 4) || '-' ||
        substr(md5(c.id::text || ':TITHE'), 17, 4) || '-' ||
        substr(md5(c.id::text || ':TITHE'), 21, 12)
    )::uuid,
    c.id,
    'TITHE',
    'Tithe collections recorded for member stewardship.',
    'WEEKLY',
    'MANUAL',
    'TITHE'
FROM churches c
WHERE NOT EXISTS (
    SELECT 1
    FROM collection_types ct
    WHERE ct.church_id = c.id
      AND ct.name = 'TITHE'
);

INSERT INTO collection_types (
    id,
    church_id,
    name,
    description,
    default_frequency,
    expected_counting_method,
    accounting_category
)
SELECT
    (
        substr(md5(c.id::text || ':THANKSGIVING'), 1, 8) || '-' ||
        substr(md5(c.id::text || ':THANKSGIVING'), 9, 4) || '-' ||
        substr(md5(c.id::text || ':THANKSGIVING'), 13, 4) || '-' ||
        substr(md5(c.id::text || ':THANKSGIVING'), 17, 4) || '-' ||
        substr(md5(c.id::text || ':THANKSGIVING'), 21, 12)
    )::uuid,
    c.id,
    'THANKSGIVING',
    'Special thanksgiving and event collections.',
    'AD_HOC',
    'MANUAL',
    'SPECIAL_OFFERING'
FROM churches c
WHERE NOT EXISTS (
    SELECT 1
    FROM collection_types ct
    WHERE ct.church_id = c.id
      AND ct.name = 'THANKSGIVING'
);
