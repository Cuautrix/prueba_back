ALTER TABLE client_validations
ALTER COLUMN ocr_ine TYPE text USING ocr_ine::text;
