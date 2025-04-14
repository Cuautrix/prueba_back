ALTER TABLE client_entity
    ADD mfa_secret VARCHAR(255);

ALTER TABLE client_entity
    ADD mfa_enabled BOOLEAN;