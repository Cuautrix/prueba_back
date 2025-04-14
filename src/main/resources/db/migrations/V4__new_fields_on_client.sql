ALTER TABLE client_entity
    ADD account_deleted BOOLEAN;

ALTER TABLE client_entity
    ADD approved_client BOOLEAN;

ALTER TABLE client_entity
    ADD approved_date TIMESTAMP WITHOUT TIME ZONE;

ALTER TABLE client_entity
    ADD client_type_id BIGINT;

ALTER TABLE client_entity
    ADD id_client_validation_status BIGINT;

ALTER TABLE client_entity
    ADD last_session_attempt TIMESTAMP WITHOUT TIME ZONE;

ALTER TABLE client_entity
    ADD nationality_id BIGINT;

ALTER TABLE client_entity
    ADD onboarding_stages VARCHAR(255);

ALTER TABLE client_entity
    ADD password_expiration_date TIMESTAMP WITHOUT TIME ZONE;

ALTER TABLE client_entity
    ADD privacy_notice_accepted BOOLEAN;

ALTER TABLE client_entity
    ADD scope VARCHAR(255);

ALTER TABLE client_entity
    ADD terms_and_conditions_accepted BOOLEAN;

ALTER TABLE client_entity
    ADD CONSTRAINT FK_CLIENT_ENTITY_ON_CLIENT_TYPE FOREIGN KEY (client_type_id) REFERENCES client_type (id);

ALTER TABLE client_entity
    ADD CONSTRAINT FK_CLIENT_ENTITY_ON_ID_CLIENT_VALIDATION_STATUS FOREIGN KEY (id_client_validation_status) REFERENCES validation_status (id);

ALTER TABLE client_entity
    ADD CONSTRAINT FK_CLIENT_ENTITY_ON_NATIONALITY FOREIGN KEY (nationality_id) REFERENCES nationality (id);

ALTER TABLE client_entity
DROP
COLUMN last_name_father;

ALTER TABLE client_entity
DROP
COLUMN last_name_mother;

ALTER TABLE client_entity
DROP
COLUMN name;

