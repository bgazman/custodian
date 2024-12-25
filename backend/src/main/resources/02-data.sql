-- Insert initial roles
INSERT INTO roles (name, description, created_at, updated_at)
VALUES
    ('ADMIN', 'Administrator with full access to the system', NOW(), NOW()),
    ('USER', 'Regular user with limited access', NOW(), NOW()),
    ('MODERATOR', 'Moderator with special permissions', NOW(), NOW());

-- Insert an admin user
INSERT INTO users (email, password, role_id, enabled, email_verified, created_at, updated_at)
VALUES
    ('admin@example.com', '$2a$10$/QQuBnMqupU5ACb1MZuoM.JuQk4WWZLnqZqIWiSqDqJ/X7mlErLaG', (SELECT id FROM roles WHERE name = 'ADMIN'), TRUE, TRUE, NOW(), NOW());

-- Insert regular users
INSERT INTO users (email, password, role_id, enabled, email_verified, created_at, updated_at)
VALUES
    ('user1@example.com', '$2a$10$/QQuBnMqupU5ACb1MZuoM.JuQk4WWZLnqZqIWiSqDqJ/X7mlErLaG', (SELECT id FROM roles WHERE name = 'USER'), TRUE, TRUE, NOW(), NOW()),
    ('user2@example.com', '$2a$10$/QQuBnMqupU5ACb1MZuoM.JuQk4WWZLnqZqIWiSqDqJ/X7mlErLaG', (SELECT id FROM roles WHERE name = 'USER'), TRUE, TRUE, NOW(), NOW()),
    ('moderator@example.com', '$2a$10$/QQuBnMqupU5ACb1MZuoM.JuQk4WWZLnqZqIWiSqDqJ/X7mlErLaG', (SELECT id FROM roles WHERE name = 'MODERATOR'), TRUE, TRUE, NOW(), NOW());


-- Insert groups
INSERT INTO groups (name, description, created_at, updated_at)
VALUES
    ('HR Team', 'Human Resources team', NOW(), NOW()),
    ('Engineering Team', 'Engineering and Development team', NOW(), NOW()),
    ('Support Team', 'Customer Support team', NOW(), NOW());


-- Add users to groups with roles
INSERT INTO group_memberships (user_id, group_id, role_id)
VALUES
    -- Admin in HR Team
    ((SELECT id FROM users WHERE email = 'admin@example.com'), (SELECT id FROM groups WHERE name = 'HR Team'), (SELECT id FROM roles WHERE name = 'ADMIN')),

    -- User1 in Engineering Team
    ((SELECT id FROM users WHERE email = 'user1@example.com'), (SELECT id FROM groups WHERE name = 'Engineering Team'), (SELECT id FROM roles WHERE name = 'USER')),

    -- User2 in Support Team
    ((SELECT id FROM users WHERE email = 'user2@example.com'), (SELECT id FROM groups WHERE name = 'Support Team'), (SELECT id FROM roles WHERE name = 'USER')),

    -- Moderator in HR Team
    ((SELECT id FROM users WHERE email = 'moderator@example.com'), (SELECT id FROM groups WHERE name = 'HR Team'), (SELECT id FROM roles WHERE name = 'MODERATOR'));

-- Insert initial permissions
INSERT INTO permissions (name, description, created_at, updated_at)
VALUES
    ('READ_PRIVILEGES', 'Allows reading of resources', NOW(), NOW()),
    ('WRITE_PRIVILEGES', 'Allows writing or modifying resources', NOW(), NOW()),
    ('DELETE_PRIVILEGES', 'Allows deletion of resources', NOW(), NOW());

-- Assign permissions to groups
INSERT INTO group_permissions (group_id, permission_id, expires_at)
VALUES
    -- HR Team permissions
    ((SELECT id FROM groups WHERE name = 'HR Team'), (SELECT id FROM permissions WHERE name = 'READ_PRIVILEGES'), NULL),
    ((SELECT id FROM groups WHERE name = 'HR Team'), (SELECT id FROM permissions WHERE name = 'WRITE_PRIVILEGES'), NULL),

    -- Engineering Team permissions
    ((SELECT id FROM groups WHERE name = 'Engineering Team'), (SELECT id FROM permissions WHERE name = 'READ_PRIVILEGES'), NULL),
    ((SELECT id FROM groups WHERE name = 'Engineering Team'), (SELECT id FROM permissions WHERE name = 'WRITE_PRIVILEGES'), NULL),

    -- Support Team permissions
    ((SELECT id FROM groups WHERE name = 'Support Team'), (SELECT id FROM permissions WHERE name = 'READ_PRIVILEGES'), NULL);

-- Insert the private key
INSERT INTO secrets (name, private_key,public_key, type, active, created_at, updated_at)
VALUES
    ('dashboard-private-key', '-----BEGIN PRIVATE KEY-----
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
                              -----END PRIVATE KEY-----','-----BEGIN PUBLIC KEY-----
                                                                                      MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA7+URQ6rrpHAACb8jMytr
                                                                                      aeX4nYx3/hLgSWcLAdDcrhjkaIG0wCIC+2kOSuw5hyPapIKQ4KyPASZRg536RR5s
                                                                                      yZAm3YfH1Y3ksj+/vHtiDVCQVghNYMJOh7U0QPMo4lNb9roFiquPJMz8kjHCzXds
                                                                                      0aUDqR/3dqliwv0hTKa0TehxXgK1VO51CtP29NFhsummGhzupV4lNfCwrHlrcvNh
                                                                                      15OEyTewS0Zu9qmeYv/yK9aM982YYYgusDcxHARnyiiXgG9bQCntOb9kAG6p18rn
                                                                                      l4hCoa2pZCj+3cWc9QoZeACqIUnsbN/RAQUyNGWmbg/yPpKbnFLR0k4wEgmpPWtZ
                                                                                      bQIDAQAB
                                                                                      -----END PUBLIC KEY-----', 'RSA_PRIVATE', TRUE, NOW(), NOW());



INSERT INTO oauth_clients (
    client_id, client_secret, redirect_uris, grant_types, scopes, token_endpoint_auth_method, algorithm, key_id, created_at, updated_at
)
VALUES (
    'iam-dashboard',
    'hashed_client_secret_value',
    '["http://localhost:8080/callback"]',
    '["authorization_code", "refresh_token"]',
    '["openid", "profile", "email", "admin"]',
    'client_secret_basic',
    'RS256',
    (SELECT id FROM secrets WHERE name = 'dashboard-private-key'),
    NOW(), NOW()
);

