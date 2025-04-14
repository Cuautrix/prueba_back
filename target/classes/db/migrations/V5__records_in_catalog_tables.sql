INSERT INTO client_type(description, "key", active, trace, created_by, created_at)
VALUES
    ('Individual with Business Activity', 'PFAE', true, 0, 'f.martinez', NOW()),
    ('Corporate Entity', 'PMORAL', true, 0, 'f.martinez', NOW());

INSERT INTO nationality(description, active, trace, created_by, created_at)
VALUES
    ('MEXICAN', true, 0, 'f.martinez', NOW()),
    ('FOREIGN', true, 0, 'f.martinez', NOW());

INSERT INTO validation_status(description, active, trace, created_by, created_at)
VALUES
    ('ONBOARDING', true, 0, 'f.martinez', NOW()),
    ('PENDING', true, 0, 'f.martinez', NOW()),
    ('IN_PROGRESS', true, 0, 'f.martinez', NOW()),
    ('WITH_OBSERVATIONS', true, 0, 'f.martinez', NOW()),
    ('WITH_CORRECTIONS', true, 0, 'f.martinez', NOW()),
    ('REJECTED', true, 0, 'f.martinez', NOW()),
    ('APPROVED', true, 0, 'f.martinez', NOW()),
    ('COMPLETED', true, 0, 'f.martinez', NOW());
