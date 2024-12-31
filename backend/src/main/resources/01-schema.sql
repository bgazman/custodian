CREATE TABLE tenants (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
	description VARCHAR(255),
    issuer_url VARCHAR(255) NOT NULL UNIQUE, -- Tenant-specific issuer
    jwks_uri VARCHAR(255) NOT NULL,         -- Tenant-specific JWKS URI
    token_lifetime INTEGER DEFAULT 3600,    -- Access token expiration
    refresh_token_lifetime INTEGER DEFAULT 86400, -- Refresh token expiration
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    enabled BOOLEAN DEFAULT FALSE,
    avatar_url TEXT,
    email_verified BOOLEAN DEFAULT FALSE,
    failed_login_attempts INTEGER DEFAULT 0,
    locked_until TIMESTAMP,
    mfa_enabled BOOLEAN DEFAULT FALSE,
    mfa_secret VARCHAR(255), -- TOTP secret key
    mfa_backup_codes JSONB DEFAULT '[]'::jsonb,
    last_login_time TIMESTAMP,
    last_password_change TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    account_non_expired BOOLEAN DEFAULT TRUE,
    account_non_locked BOOLEAN DEFAULT TRUE,
    credentials_non_expired BOOLEAN DEFAULT TRUE,
    deleted_at TIMESTAMP DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE IF NOT EXISTS tenant_users (
    tenant_id BIGINT NOT NULL,        -- References the tenant
    user_id BIGINT NOT NULL,          -- References the user
    role VARCHAR(50),                 -- Optional: Role for the user in the tenant
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- When the user joined the tenant
    PRIMARY KEY (tenant_id, user_id), -- Composite key for uniqueness
    FOREIGN KEY (tenant_id) REFERENCES tenants(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE, -- Role name (e.g., "ADMIN", "USER")
    description VARCHAR(255),         -- Optional: Description of the role
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    parent_role_id BIGINT DEFAULT NULL REFERENCES roles(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS groups (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    tenant_id BIGINT REFERENCES tenants(id) ON DELETE CASCADE,
    parent_group_id BIGINT DEFAULT NULL REFERENCES groups(id) ON DELETE CASCADE

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

CREATE TABLE IF NOT EXISTS role_permissions (
    role_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
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
    name VARCHAR(255) NOT NULL,
    tenant_id BIGINT NOT NULL,
    application_type VARCHAR(50) NOT NULL DEFAULT 'web',  -- Added application_type
    response_types JSONB NOT NULL DEFAULT '["code"]'::jsonb,  -- Added response_types
    client_id VARCHAR(100) NOT NULL UNIQUE,
    status VARCHAR(50) DEFAULT 'active',
    revoked_at TIMESTAMP DEFAULT NULL,
    client_secret VARCHAR(255) NOT NULL,
    client_secret_last_rotated TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    redirect_uris JSONB NOT NULL,
    grant_types JSONB NOT NULL,
    scopes JSONB DEFAULT '["openid","profile","email"]'::jsonb,
    token_endpoint_auth_method VARCHAR(50) DEFAULT 'client_secret_basic',
    algorithm VARCHAR(10) NOT NULL DEFAULT 'RS256',
    key_id BIGINT, -- Single key reference
    access_token_expiry_seconds INTEGER NOT NULL DEFAULT 3600,
    refresh_token_expiry_seconds INTEGER NOT NULL DEFAULT 86400,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP DEFAULT NULL,
    CONSTRAINT fk_key_id FOREIGN KEY (key_id) REFERENCES secrets (id) ON DELETE CASCADE,
    CONSTRAINT fk_tenant_id FOREIGN KEY (tenant_id) REFERENCES tenants (id) ON DELETE CASCADE -- Relationship added

);

CREATE TABLE IF NOT EXISTS tokens (
    id BIGSERIAL PRIMARY KEY,
    client_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    token_type VARCHAR(50) NOT NULL, -- 'access_token', 'refresh_token'
    token TEXT NOT NULL, -- Should be hashed
    expires_at TIMESTAMP NOT NULL,
    issued_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    revoked_at TIMESTAMP DEFAULT NULL,
    rotated_to BIGINT NULL, -- For tracking rotation
    CONSTRAINT fk_client_id FOREIGN KEY (client_id) REFERENCES oauth_clients (id) ON DELETE CASCADE
);

CREATE UNIQUE INDEX users_email_idx ON users (email);
CREATE INDEX users_enabled_idx ON users (enabled);
CREATE INDEX users_login_attempts_idx ON users (failed_login_attempts, locked_until);

CREATE INDEX idx_tenant_users_tenant_id ON tenant_users(tenant_id);
CREATE INDEX idx_oauth_clients_client_id ON oauth_clients(client_id);

CREATE UNIQUE INDEX roles_name_idx ON roles (name);

-- User Roles Table
CREATE INDEX user_roles_user_idx ON user_roles (user_id);
CREATE INDEX user_roles_role_idx ON user_roles (role_id);

-- Groups Table

CREATE UNIQUE INDEX groups_name_idx ON groups (name);

-- Group Memberships Table
CREATE INDEX group_memberships_user_idx ON group_memberships (user_id);
CREATE INDEX group_memberships_group_idx ON group_memberships (group_id);

-- Permissions Table
CREATE UNIQUE INDEX permissions_name_idx ON permissions (name);

-- Group Permissions Table
CREATE INDEX group_permissions_group_idx ON group_permissions (group_id);
CREATE INDEX group_permissions_permission_idx ON group_permissions (permission_id);

-- Secrets Table
CREATE INDEX idx_secrets_name ON secrets (name);
CREATE INDEX idx_secrets_active_expires ON secrets (active, expires_at);


