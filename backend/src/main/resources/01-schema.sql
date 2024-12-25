CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role_id BIGINT, -- Foreign key reference to the roles table
    enabled BOOLEAN DEFAULT FALSE,
    email_verified BOOLEAN DEFAULT FALSE,
    failed_login_attempts INTEGER DEFAULT 0,
    locked_until TIMESTAMP,
    mfa_enabled BOOLEAN DEFAULT FALSE,
    mfa_secret VARCHAR(255), -- TOTP secret key
    mfa_backup_codes JSONB DEFAULT '[]',
    last_login_time TIMESTAMP,
    last_password_change TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    account_non_expired BOOLEAN DEFAULT TRUE,
    account_non_locked BOOLEAN DEFAULT TRUE,
    credentials_non_expired BOOLEAN DEFAULT TRUE,
    deleted_at TIMESTAMP DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE, -- Role name (e.g., "ADMIN", "USER")
    description VARCHAR(255),         -- Optional: Description of the role
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS groups (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    parent_group_id BIGINT DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);



CREATE TABLE IF NOT EXISTS group_memberships (
    user_id BIGINT NOT NULL,
    group_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL, -- Reference to the roles table
    PRIMARY KEY (user_id, group_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (group_id) REFERENCES groups(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
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
    expires_at TIMESTAMP DEFAULT NULL,
    PRIMARY KEY (group_id, permission_id),
    FOREIGN KEY (group_id) REFERENCES groups(id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS secrets (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE, -- Unique identifier for the secret
    public_key TEXT NOT NULL, -- Key value (encrypted for private keys, raw for public keys)
    private_key TEXT NOT NULL, -- Key value (encrypted for private keys, raw for public keys)
    type VARCHAR(50) NOT NULL, -- Example: "RSA", "SYMMETRIC"
    last_rotated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    active BOOLEAN DEFAULT TRUE,
    expires_at TIMESTAMP DEFAULT NULL, -- Optional expiration date
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE IF NOT EXISTS oauth_clients (
    id BIGSERIAL PRIMARY KEY,
    client_id VARCHAR(100) NOT NULL UNIQUE,
    client_secret VARCHAR(255) NOT NULL,
    redirect_uris JSONB NOT NULL,
    grant_types JSONB NOT NULL,
    scopes JSONB,
    token_endpoint_auth_method VARCHAR(50) DEFAULT 'client_secret_basic',
    algorithm VARCHAR(10) NOT NULL DEFAULT 'RS256',
    key_id BIGINT, -- Single key reference
    access_token_expiry_seconds INTEGER NOT NULL DEFAULT 3600,
    refresh_token_expiry_seconds INTEGER NOT NULL DEFAULT 86400,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP DEFAULT NULL,
    CONSTRAINT fk_key_id FOREIGN KEY (key_id) REFERENCES secrets (id) ON DELETE CASCADE
);



-- Users Table
CREATE INDEX idx_users_email ON users (email);
CREATE INDEX idx_users_deleted_at ON users (deleted_at);
CREATE INDEX idx_users_role_id ON users (role_id);

-- Roles Table
CREATE INDEX idx_roles_name ON roles (name);

-- Groups Table
CREATE INDEX idx_groups_name ON groups (name);
CREATE INDEX idx_groups_parent_group_id ON groups (parent_group_id);

-- Group Memberships Table
CREATE INDEX idx_group_memberships_user_id ON group_memberships (user_id);
CREATE INDEX idx_group_memberships_group_id ON group_memberships (group_id);
CREATE INDEX idx_group_memberships_role_id ON group_memberships (role_id);

-- Permissions Table
CREATE INDEX idx_permissions_name ON permissions (name);

-- Group Permissions Table
CREATE INDEX idx_group_permissions_group_id ON group_permissions (group_id);
CREATE INDEX idx_group_permissions_permission_id ON group_permissions (permission_id);
CREATE INDEX idx_group_permissions_expires_at ON group_permissions (expires_at);

-- Secrets Table
CREATE INDEX idx_secrets_name ON secrets (name);
CREATE INDEX idx_secrets_active_expires ON secrets (active, expires_at);

-- OAuth Clients Table
CREATE INDEX idx_oauth_clients_client_id ON oauth_clients (client_id);
CREATE INDEX idx_oauth_clients_key_id ON oauth_clients (key_id);
