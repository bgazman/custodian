CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50),
    enabled BOOLEAN DEFAULT FALSE,
    email_verified BOOLEAN DEFAULT FALSE,
    failed_login_attempts INTEGER DEFAULT 0,
    locked_until TIMESTAMP,
    last_login_time TIMESTAMP,
    last_password_change TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    account_non_expired BOOLEAN DEFAULT TRUE,
    account_non_locked BOOLEAN DEFAULT TRUE,
    credentials_non_expired BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE IF NOT EXISTS groups (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);



CREATE TABLE IF NOT EXISTS group_memberships (
    user_id BIGINT NOT NULL,
    group_id BIGINT NOT NULL,
    role VARCHAR(50) NOT NULL, -- Example: "ADMIN", "MEMBER"
    PRIMARY KEY (user_id, group_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (group_id) REFERENCES groups(id) ON DELETE CASCADE
);



CREATE TABLE IF NOT EXISTS permissions (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE, -- Example: "READ_PRIVILEGES", "WRITE_PRIVILEGES"
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);



CREATE TABLE IF NOT EXISTS group_permissions (
    group_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    PRIMARY KEY (group_id, permission_id),
    FOREIGN KEY (group_id) REFERENCES groups(id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS token_configuration (
    id BIGSERIAL PRIMARY KEY,
    app_name VARCHAR(100) NOT NULL UNIQUE, -- "GLOBAL", "APP1", "APP2", etc.
    access_token_expiration_minutes INT DEFAULT 15, -- Default access token validity
    refresh_token_expiration_minutes INT DEFAULT 10080, -- Default refresh token validity (7 days)
    secret_key VARCHAR(255) NOT NULL, -- Secret for signing tokens
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


-- If you want to add indexes (recommended):
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_locked_until ON users(locked_until);

-- Add an index on `username` for quick lookups in the users table
--CREATE INDEX idx_users_username ON users(username);

-- Add an index on `name` for quick lookups in the groups table
CREATE INDEX idx_groups_name ON groups(name);

-- Add a composite index on group memberships for frequent queries
CREATE INDEX idx_group_memberships_user_group ON group_memberships(user_id, group_id);

-- Add an index on `name` for quick lookups in the permissions table
CREATE INDEX idx_permissions_name ON permissions(name);

-- Add a composite index on group permissions for frequent queries
CREATE INDEX idx_group_permissions_group_permission ON group_permissions(group_id, permission_id);
