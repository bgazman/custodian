INSERT INTO users (email, password, role, enabled, email_verified, failed_login_attempts, mfa_enabled, mfa_secret, mfa_backup_codes, last_login_time, account_non_expired, account_non_locked, credentials_non_expired)
VALUES
    ('admin@example.com', '$2a$10$/QQuBnMqupU5ACb1MZuoM.JuQk4WWZLnqZqIWiSqDqJ/X7mlErLaG', 'ADMIN', TRUE, TRUE, 0, FALSE, NULL, '[]', CURRENT_TIMESTAMP, TRUE, TRUE, TRUE),
    ('user1@example.com', '$2a$10$/QQuBnMqupU5ACb1MZuoM.JuQk4WWZLnqZqIWiSqDqJ/X7mlErLaG', 'USER', TRUE, TRUE, 0, TRUE, 'TOTPSECRET123', '["CODE1", "CODE2"]', CURRENT_TIMESTAMP, TRUE, TRUE, TRUE),
    ('user2@example.com', '$2a$10$/QQuBnMqupU5ACb1MZuoM.JuQk4WWZLnqZqIWiSqDqJ/X7mlErLaG', 'USER', FALSE, FALSE, 3, FALSE, NULL, '[]', NULL, TRUE, TRUE, TRUE);

INSERT INTO groups (name, description)
VALUES
    ('Admins', 'Administrative users with full access'),
    ('Users', 'General application users'),
    ('Support', 'Customer support team');

INSERT INTO group_memberships (user_id, group_id, role)
VALUES
    (1, 1, 'ADMIN'), -- Admin user is part of Admins group
    (2, 2, 'MEMBER'), -- User1 is part of Users group
    (3, 3, 'MEMBER'); -- User2 is part of Support group


INSERT INTO permissions (name, description)
VALUES
    ('READ_PRIVILEGES', 'Allows reading data'),
    ('WRITE_PRIVILEGES', 'Allows modifying data'),
    ('DELETE_PRIVILEGES', 'Allows deleting data');

INSERT INTO group_permissions (group_id, permission_id)
VALUES
    (1, 1), -- Admins group has READ_PRIVILEGES
    (1, 2), -- Admins group has WRITE_PRIVILEGES
    (1, 3), -- Admins group has DELETE_PRIVILEGES
    (2, 1), -- Users group has READ_PRIVILEGES
    (3, 1), -- Support group has READ_PRIVILEGES
    (3, 2); -- Support group has WRITE_PRIVILEGES
-- Insert Private Keys into secrets table
INSERT INTO secrets (name, value, metadata)
VALUES
    ('private_key_global',
     '-----BEGIN PRIVATE KEY-----
MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDv5RFDquukcAAJ
vyMzK2tp5fidjHf+EuBJZwsB0NyuGORogbTAIgL7aQ5K7DmHI9qkgpDgrI8BJlGD
nfpFHmzJkCbdh8fVjeSyP7+8e2INUJBWCE1gwk6HtTRA8yjiU1v2ugWKq48kzPyS
McLNd2zRpQOpH/d2qWLC/SFMprRN6HFeArVU7nUK0/b00WGy6aYaHO6lXiU18LCs
eWty82HXk4TJN7BLRm72qZ5i//Ir1oz3zZhhiC6wNzEcBGfKKJeAb1tAKe05v2QA
bqnXyueXiEKhralkKP7dxZz1Chl4AKohSexs39EBBTI0ZaZuD/I+kpucUtHSTjAS
Cak9a1ltAgMBAAECggEAJDqygEcsXkGh59brcHtitfQKt3Ry3LaqE7vRoD0UTuCo
ghXwn8GjNKfZhjwltgCWsRic26mD0VchoWB23wBoyooQLI2ogtpyiu7wvHzfwoYD
vlJzqSS/KmSV1ydB6ehP77OJSbd+Hz8r98I3GsOp9gFXS6/ttgh/x1XmNQbKHSeu
NMcpX9LsVqc64sWJFFwRPhd7YZ9F0aOFwRNBKpG35m9IDmzJmxvdOgGDmF77MHfq
pLhfNNhbALNC11oG2xWMiWJlQBwGYh93EA0v4ishDFVM9iRq/ZYDHrIFP5ShhyJV
fkP1LybMdIH+Y1sPvp3heD+cADz3kmCkSCOGGgtv2wKBgQDymjk6fZ3X0RzzBld2
8t3ABNtQGVhQC1gEfSQ9ms7uSAszRNULFbF3xPxCyh897em+rzUuAnI0Ak4ziWbO
O6EAHuYIdF0ZaOY0sOSLyimLS0v0+e8+xddvMdbJfZgsFk0uh3x3vYjzJAoLFQfw
2600Bl0b0FqOz1WD+yjI3b6YJwKBgQD9JJCV6MdyfopLJq7kgafwvD+nflytJHUf
rtxHfn8zMP2kkI8UsM3Y5Xw5cfzRYVg1MoTjpbjN1zkKv4IJAbWgzDFnhuAY2+I+
6C0RvsRqRBQ7C/lpT+iOc1luOleA1PJbjSSSISjCYvNlXu5OPG7EoXpbzzQoSLYN
cDwIJwrKSwKBgA9q97oc45wkoQdc3tAjbS5X4Kw9gBxOLEDUxynXHdGM6JFQjY2P
ymCHN1TzTXr9FL7WmbWb2DAv+VJTKCJxLbE5RXtCEeycewzcNRohNfSxS1l9TNQb
dK6PZ0wU3Efo/uy3FZTyibKSFsVPh9qpy18kDGTJIAg1awThQF1LomI/AoGBAJB9
7omi9TVFRWrgCbQkzdd7nFnepsetp60OAm5Mpg5ySFVGrmUBBn5nfyDLD6P0DIVQ
8MmALpxP3R9lcLPzZdkCtLCDLCcEW3/Mxuz6FuMROPr7OsIB+pxU+xcP9iJzamMX
jWhbxQgvsv6J0TyXAiL6iP0eqIbV+hRxm9KLdn1BAoGBAMwi/BRuvHtR4GBIKSRP
/5gYEVAORNlWjf329U+SLXfs39i0LAmpObI7BwT0Tmc5yg4bDETtJIVcbolS/kOb
QXmMe2SJd9BBNP3OsE3xG3OFFh8VLDuJ5OwAG7cD1MAdGeZABlw7kZqm/SH4po1K
wXpHqsO7mNTLCoMVu0p9W5tS
-----END PRIVATE KEY-----',
     '{"usage": "token signing", "app": "GLOBAL"}'),

    ('private_key_app1',
     '-----BEGIN PRIVATE KEY-----
MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDv5RFDquukcAAJ
vyMzK2tp5fidjHf+EuBJZwsB0NyuGORogbTAIgL7aQ5K7DmHI9qkgpDgrI8BJlGD
nfpFHmzJkCbdh8fVjeSyP7+8e2INUJBWCE1gwk6HtTRA8yjiU1v2ugWKq48kzPyS
McLNd2zRpQOpH/d2qWLC/SFMprRN6HFeArVU7nUK0/b00WGy6aYaHO6lXiU18LCs
eWty82HXk4TJN7BLRm72qZ5i//Ir1oz3zZhhiC6wNzEcBGfKKJeAb1tAKe05v2QA
bqnXyueXiEKhralkKP7dxZz1Chl4AKohSexs39EBBTI0ZaZuD/I+kpucUtHSTjAS
Cak9a1ltAgMBAAECggEAJDqygEcsXkGh59brcHtitfQKt3Ry3LaqE7vRoD0UTuCo
ghXwn8GjNKfZhjwltgCWsRic26mD0VchoWB23wBoyooQLI2ogtpyiu7wvHzfwoYD
vlJzqSS/KmSV1ydB6ehP77OJSbd+Hz8r98I3GsOp9gFXS6/ttgh/x1XmNQbKHSeu
NMcpX9LsVqc64sWJFFwRPhd7YZ9F0aOFwRNBKpG35m9IDmzJmxvdOgGDmF77MHfq
pLhfNNhbALNC11oG2xWMiWJlQBwGYh93EA0v4ishDFVM9iRq/ZYDHrIFP5ShhyJV
fkP1LybMdIH+Y1sPvp3heD+cADz3kmCkSCOGGgtv2wKBgQDymjk6fZ3X0RzzBld2
8t3ABNtQGVhQC1gEfSQ9ms7uSAszRNULFbF3xPxCyh897em+rzUuAnI0Ak4ziWbO
O6EAHuYIdF0ZaOY0sOSLyimLS0v0+e8+xddvMdbJfZgsFk0uh3x3vYjzJAoLFQfw
2600Bl0b0FqOz1WD+yjI3b6YJwKBgQD9JJCV6MdyfopLJq7kgafwvD+nflytJHUf
rtxHfn8zMP2kkI8UsM3Y5Xw5cfzRYVg1MoTjpbjN1zkKv4IJAbWgzDFnhuAY2+I+
6C0RvsRqRBQ7C/lpT+iOc1luOleA1PJbjSSSISjCYvNlXu5OPG7EoXpbzzQoSLYN
cDwIJwrKSwKBgA9q97oc45wkoQdc3tAjbS5X4Kw9gBxOLEDUxynXHdGM6JFQjY2P
ymCHN1TzTXr9FL7WmbWb2DAv+VJTKCJxLbE5RXtCEeycewzcNRohNfSxS1l9TNQb
dK6PZ0wU3Efo/uy3FZTyibKSFsVPh9qpy18kDGTJIAg1awThQF1LomI/AoGBAJB9
7omi9TVFRWrgCbQkzdd7nFnepsetp60OAm5Mpg5ySFVGrmUBBn5nfyDLD6P0DIVQ
8MmALpxP3R9lcLPzZdkCtLCDLCcEW3/Mxuz6FuMROPr7OsIB+pxU+xcP9iJzamMX
jWhbxQgvsv6J0TyXAiL6iP0eqIbV+hRxm9KLdn1BAoGBAMwi/BRuvHtR4GBIKSRP
/5gYEVAORNlWjf329U+SLXfs39i0LAmpObI7BwT0Tmc5yg4bDETtJIVcbolS/kOb
QXmMe2SJd9BBNP3OsE3xG3OFFh8VLDuJ5OwAG7cD1MAdGeZABlw7kZqm/SH4po1K
wXpHqsO7mNTLCoMVu0p9W5tS
-----END PRIVATE KEY-----',
     '{"usage": "token signing", "app": "APP1"}'),

    ('jwt_secret_app2',
     'symmetric-secret-key-for-app2',
     '{"usage": "HS256 token signing", "app": "APP2"}');
-- Insert Token Configurations
INSERT INTO token_configuration (
    app_name,
    access_token_expiration_minutes,
    refresh_token_expiration_minutes,
    secret_id,
    private_key_id,
    key_id,
    public_key,
    algorithm
)
VALUES
    -- Configuration for GLOBAL (asymmetric: RS256)
    ('GLOBAL',
     15,
     10080,
     NULL,
     (SELECT id FROM secrets WHERE name = 'private_key_global'),
     'key1',
     '-----BEGIN PUBLIC KEY-----
MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA7+URQ6rrpHAACb8jMytr
aeX4nYx3/hLgSWcLAdDcrhjkaIG0wCIC+2kOSuw5hyPapIKQ4KyPASZRg536RR5s
yZAm3YfH1Y3ksj+/vHtiDVCQVghNYMJOh7U0QPMo4lNb9roFiquPJMz8kjHCzXds
0aUDqR/3dqliwv0hTKa0TehxXgK1VO51CtP29NFhsummGhzupV4lNfCwrHlrcvNh
15OEyTewS0Zu9qmeYv/yK9aM982YYYgusDcxHARnyiiXgG9bQCntOb9kAG6p18rn
l4hCoa2pZCj+3cWc9QoZeACqIUnsbN/RAQUyNGWmbg/yPpKbnFLR0k4wEgmpPWtZ
bQIDAQAB
-----END PUBLIC KEY-----',
     'RS256'),

    -- Configuration for APP1 (asymmetric: RS256)
    ('APP1',
     30,
     1440,
     NULL,
     (SELECT id FROM secrets WHERE name = 'private_key_app1'),
     'key2',
     '-----BEGIN PUBLIC KEY-----
MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA7+URQ6rrpHAACb8jMytr
aeX4nYx3/hLgSWcLAdDcrhjkaIG0wCIC+2kOSuw5hyPapIKQ4KyPASZRg536RR5s
yZAm3YfH1Y3ksj+/vHtiDVCQVghNYMJOh7U0QPMo4lNb9roFiquPJMz8kjHCzXds
0aUDqR/3dqliwv0hTKa0TehxXgK1VO51CtP29NFhsummGhzupV4lNfCwrHlrcvNh
15OEyTewS0Zu9qmeYv/yK9aM982YYYgusDcxHARnyiiXgG9bQCntOb9kAG6p18rn
l4hCoa2pZCj+3cWc9QoZeACqIUnsbN/RAQUyNGWmbg/yPpKbnFLR0k4wEgmpPWtZ
bQIDAQAB
-----END PUBLIC KEY-----',
     'RS256'),

    -- Configuration for APP2 (symmetric: HS256)
    ('APP2',
     10,
     4320,
     (SELECT id FROM secrets WHERE name = 'jwt_secret_app2'),
     NULL,
     'key3',
     NULL,
     'HS256');

INSERT INTO oauth_clients (client_id, client_secret, redirect_uris, grant_types, scopes, token_endpoint_auth_method)
VALUES
    ('client1', 'encrypted_client_secret1', '["https://app1.example.com/callback"]', '["authorization_code", "client_credentials"]', '["read", "write"]', 'client_secret_basic'),
    ('client2', 'encrypted_client_secret2', '["https://app2.example.com/callback"]', '["password", "refresh_token"]', '["read"]', 'client_secret_post');

