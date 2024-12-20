INSERT INTO users (email, password, enabled, email_verified, account_non_expired, account_non_locked, credentials_non_expired, role, failed_login_attempts, created_at, updated_at)
VALUES
('admin@custodian.com', '$2b$12$NbGuReHz40wBttPxCvmCB.otdwWu0nk7x6DphDeJKikmrGYZ3kdAC', TRUE, TRUE, TRUE, TRUE, TRUE, 'ADMIN', 0, NOW(), NOW());

INSERT INTO groups (name, description, created_at, updated_at)
VALUES
('Admin', 'Administrators with full system access', NOW(), NOW());

INSERT INTO group_memberships (user_id, group_id, role)
VALUES
((SELECT id FROM users WHERE email = 'admin@custodian.com'),
 (SELECT id FROM groups WHERE name = 'Admin'),
 'ADMIN');

INSERT INTO permissions (name, description, created_at, updated_at)
VALUES
('READ_PRIVILEGES', 'Permission to read data', NOW(), NOW()),
('WRITE_PRIVILEGES', 'Permission to write data', NOW(), NOW()),
('MANAGE_USERS', 'Permission to manage users', NOW(), NOW());

INSERT INTO group_permissions (group_id, permission_id)
VALUES
((SELECT id FROM groups WHERE name = 'Admin'), (SELECT id FROM permissions WHERE name = 'READ_PRIVILEGES')),
((SELECT id FROM groups WHERE name = 'Admin'), (SELECT id FROM permissions WHERE name = 'WRITE_PRIVILEGES')),
((SELECT id FROM groups WHERE name = 'Admin'), (SELECT id FROM permissions WHERE name = 'MANAGE_USERS'));

INSERT INTO token_configuration (app_name, access_token_expiration_minutes, refresh_token_expiration_minutes, secret_key, created_at, updated_at)
VALUES
('GLOBAL', 15, 10080, 'N1JtYk9zU0ZYYkFsT0NYeVZ4VmVpR1JIdE82eHhQVGJWcHFqSkhPSWJKZ3YwVlpJ', NOW(), NOW());

INSERT INTO token_configuration (app_name, access_token_expiration_minutes, refresh_token_expiration_minutes, secret_key, created_at, updated_at)
VALUES
('APP1', 30, 1440, 'TWFiN2lLcUtTUlZZQU5nY0FyVkd2UVlYTzRVckhVQmtaUlM2ck5EMFJaWnRwdTVn', NOW(), NOW()),
('APP2', 60, 20160, 'Zkxwc1ZocXF0dGx3d2tqZktEQUF3RU5uRHhUZ3NVYnd3bldIUG9DbkZWVndHWTk=', NOW(), NOW());

