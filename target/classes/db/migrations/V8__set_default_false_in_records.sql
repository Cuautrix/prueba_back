ALTER TABLE client_entity
    ALTER COLUMN mfa_enabled SET DEFAULT FALSE;

UPDATE client_entity
SET mfa_enabled = FALSE
WHERE mfa_enabled IS NULL;

ALTER TABLE client_entity
    ALTER COLUMN account_locked SET DEFAULT FALSE;

UPDATE client_entity
SET account_locked = FALSE
WHERE account_locked IS NULL;
