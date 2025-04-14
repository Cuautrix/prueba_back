CREATE SEQUENCE IF NOT EXISTS password_reset_token_seq START WITH 1 INCREMENT BY 50;

ALTER TABLE client_session
    ADD latitude DECIMAL(10, 7);

ALTER TABLE client_session
    ADD longitude DECIMAL(10, 7);

ALTER TABLE client_session
DROP
COLUMN latitud;

ALTER TABLE client_session
DROP
COLUMN longitud;