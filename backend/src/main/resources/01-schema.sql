CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    enabled BOOLEAN DEFAULT FALSE,
    avatar_url TEXT,
    failed_login_attempts INTEGER DEFAULT 0,
    locked_until TIMESTAMP,
    phone_number VARCHAR(15) DEFAULT NULL,
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

CREATE TABLE IF NOT EXISTS user_attributes (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    key VARCHAR(255) NOT NULL,
    value VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, key) -- Ensure unique attributes per user
);
CREATE TABLE IF NOT EXISTS roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    parent_role_id BIGINT DEFAULT NULL REFERENCES roles(id) ON DELETE CASCADE
);
CREATE TABLE IF NOT EXISTS policies (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description VARCHAR(255),
    definition JSONB NOT NULL,
    effect VARCHAR(10) NOT NULL DEFAULT 'allow',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
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
    parent_group_id BIGINT DEFAULT NULL REFERENCES groups(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS group_memberships (
    user_id BIGINT NOT NULL,
    group_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, group_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (group_id) REFERENCES groups(id) ON DELETE CASCADE
);


CREATE TABLE policy_assignments (
    id BIGSERIAL PRIMARY KEY,
    policy_id BIGINT NOT NULL REFERENCES policies(id) ON DELETE CASCADE,
    user_id BIGINT DEFAULT NULL REFERENCES users(id) ON DELETE CASCADE,
    role_id BIGINT DEFAULT NULL REFERENCES roles(id) ON DELETE CASCADE,
    group_id BIGINT DEFAULT NULL REFERENCES groups(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE IF NOT EXISTS permissions (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
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
    name VARCHAR(255) NOT NULL UNIQUE,
    public_key TEXT NOT NULL,
    private_key TEXT NOT NULL,
    type VARCHAR(50) NOT NULL,
    last_rotated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    active BOOLEAN DEFAULT TRUE,
    expires_at TIMESTAMP DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);




CREATE TABLE IF NOT EXISTS oauth_clients (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    application_type VARCHAR(50) NOT NULL DEFAULT 'web',
    response_types JSONB NOT NULL DEFAULT '["code"]'::jsonb,
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
    key_id BIGINT REFERENCES secrets(id) ON DELETE CASCADE,
    access_token_expiry_seconds INTEGER NOT NULL DEFAULT 3600,
    refresh_token_expiry_seconds INTEGER NOT NULL DEFAULT 86400,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP DEFAULT NULL
);
CREATE TABLE IF NOT EXISTS user_client_registrations (
    user_id BIGINT NOT NULL REFERENCES users(id),
    client_id BIGINT NOT NULL REFERENCES oauth_clients(id),  --
    email_verified BOOLEAN DEFAULT FALSE,
    mfa_enabled BOOLEAN DEFAULT FALSE,
    mfa_method VARCHAR(50),
    consent_granted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_used_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    -- Add unique constraint to prevent duplicate registrations
    PRIMARY KEY (user_id, client_id)
);

CREATE TABLE IF NOT EXISTS tokens (
    id BIGSERIAL PRIMARY KEY,
    client_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    token_type VARCHAR(50) NOT NULL,
    token TEXT NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    issued_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    revoked_at TIMESTAMP DEFAULT NULL,
    rotated_to BIGINT NULL,
    FOREIGN KEY (client_id) REFERENCES oauth_clients(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS resources (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(50),
    description VARCHAR(255),
    attributes JSONB DEFAULT '{}'::jsonb,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS resource_permissions (
    resource_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (resource_id, permission_id),
    FOREIGN KEY (resource_id) REFERENCES resources(id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE
);

-- Users Table Indexes
CREATE INDEX idx_users_email ON users (email);
CREATE INDEX idx_users_enabled ON users (enabled);
CREATE INDEX idx_users_last_login_time ON users (last_login_time);
CREATE INDEX idx_users_created_at ON users (created_at);
CREATE INDEX idx_users_updated_at ON users (updated_at);

-- User Attributes Table Indexes
CREATE INDEX idx_user_attributes_user_id ON user_attributes (user_id);
CREATE INDEX idx_user_attributes_key ON user_attributes (key);

-- Roles Table Indexes
CREATE INDEX idx_roles_name ON roles (name);
CREATE INDEX idx_roles_created_at ON roles (created_at);
CREATE INDEX idx_roles_updated_at ON roles (updated_at);

-- Policies Table Indexes
CREATE INDEX idx_policies_name ON policies (name);
CREATE INDEX idx_policies_created_at ON policies (created_at);
CREATE INDEX idx_policies_updated_at ON policies (updated_at);

-- User Roles Table Indexes
CREATE INDEX idx_user_roles_user_id ON user_roles (user_id);
CREATE INDEX idx_user_roles_role_id ON user_roles (role_id);

-- Groups Table Indexes
CREATE INDEX idx_groups_name ON groups (name);
CREATE INDEX idx_groups_created_at ON groups (created_at);
CREATE INDEX idx_groups_updated_at ON groups (updated_at);

-- Group Memberships Table Indexes
CREATE INDEX idx_group_memberships_user_id ON group_memberships (user_id);
CREATE INDEX idx_group_memberships_group_id ON group_memberships (group_id);

-- Policy Assignments Table Indexes
CREATE INDEX idx_policy_assignments_policy_id ON policy_assignments (policy_id);
CREATE INDEX idx_policy_assignments_user_id ON policy_assignments (user_id);
CREATE INDEX idx_policy_assignments_role_id ON policy_assignments (role_id);
CREATE INDEX idx_policy_assignments_group_id ON policy_assignments (group_id);

-- Permissions Table Indexes
CREATE INDEX idx_permissions_name ON permissions (name);
CREATE INDEX idx_permissions_created_at ON permissions (created_at);
CREATE INDEX idx_permissions_updated_at ON permissions (updated_at);

-- Group Permissions Table Indexes
CREATE INDEX idx_group_permissions_group_id ON group_permissions (group_id);
CREATE INDEX idx_group_permissions_permission_id ON group_permissions (permission_id);

-- Role Permissions Table Indexes
CREATE INDEX idx_role_permissions_role_id ON role_permissions (role_id);
CREATE INDEX idx_role_permissions_permission_id ON role_permissions (permission_id);

-- Secrets Table Indexes
CREATE INDEX idx_secrets_name ON secrets (name);
CREATE INDEX idx_secrets_created_at ON secrets (created_at);
CREATE INDEX idx_secrets_updated_at ON secrets (updated_at);



-- OAuth Clients Table Indexes
CREATE INDEX idx_oauth_clients_name ON oauth_clients (name);
CREATE INDEX idx_oauth_clients_client_id ON oauth_clients (client_id);
CREATE INDEX idx_oauth_clients_status ON oauth_clients (status);
CREATE INDEX idx_oauth_clients_created_at ON oauth_clients (created_at);
CREATE INDEX idx_oauth_clients_updated_at ON oauth_clients (updated_at);

-- User Clients Registrations Table Indexes
CREATE INDEX idx_user_client_reg_user_id ON user_client_registrations(user_id);
CREATE INDEX idx_user_client_reg_client_id ON user_client_registrations(client_id);
CREATE INDEX idx_user_client_reg_lookup ON user_client_registrations(user_id, client_id, email_verified);
-- Tokens Table Indexes
CREATE INDEX idx_tokens_client_id ON tokens (client_id);
CREATE INDEX idx_tokens_user_id ON tokens (user_id);
CREATE INDEX idx_tokens_expires_at ON tokens (expires_at);

-- Resources Table Indexes
CREATE INDEX idx_resources_name ON resources (name);
CREATE INDEX idx_resources_type ON resources (type);
CREATE INDEX idx_resources_created_at ON resources (created_at);
CREATE INDEX idx_resources_updated_at ON resources (updated_at);

-- Resource Permissions Table Indexes
CREATE INDEX idx_resource_permissions_resource_id ON resource_permissions (resource_id);
CREATE INDEX idx_resource_permissions_permission_id ON resource_permissions (permission_id);
