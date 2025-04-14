ALTER TABLE client_validations
    ALTER COLUMN lista_69b TYPE text USING lista_69b::text,
    ALTER COLUMN rfc TYPE text USING rfc::text,
    ALTER COLUMN curp TYPE text USING curp::text,
    ALTER COLUMN datos_fiscales TYPE text USING datos_fiscales::text;