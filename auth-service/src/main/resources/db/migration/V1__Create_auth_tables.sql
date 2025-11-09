-- Auth Service Schema Migration
-- Create users table

CREATE TABLE IF NOT EXISTS auth.users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    email VARCHAR(255) UNIQUE NOT NULL,
    phone VARCHAR(20),
    password_hash VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    is_active BOOLEAN DEFAULT true,
    is_verified BOOLEAN DEFAULT false,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_login_at TIMESTAMP,
    profile_json JSONB,
    CONSTRAINT check_role CHECK (role IN ('STUDENT', 'TEACHER', 'ADMIN', 'GUARDIAN', 'CONTENT_CREATOR'))
);

-- Create indexes
CREATE INDEX idx_users_email ON auth.users(email);
CREATE INDEX idx_users_phone ON auth.users(phone);
CREATE INDEX idx_users_role ON auth.users(role);
CREATE INDEX idx_users_created_at ON auth.users(created_at);

-- Create refresh tokens table
CREATE TABLE IF NOT EXISTS auth.refresh_tokens (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    token VARCHAR(512) UNIQUE NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    revoked BOOLEAN DEFAULT false,
    revoked_at TIMESTAMP,
    device_info VARCHAR(512)
);

CREATE INDEX idx_refresh_tokens_user_id ON auth.refresh_tokens(user_id);
CREATE INDEX idx_refresh_tokens_token ON auth.refresh_tokens(token);
CREATE INDEX idx_refresh_tokens_expires_at ON auth.refresh_tokens(expires_at);

-- Create password reset tokens table
CREATE TABLE IF NOT EXISTS auth.password_reset_tokens (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    token VARCHAR(512) UNIQUE NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    used BOOLEAN DEFAULT false,
    used_at TIMESTAMP
);

CREATE INDEX idx_password_reset_tokens_user_id ON auth.password_reset_tokens(user_id);
CREATE INDEX idx_password_reset_tokens_token ON auth.password_reset_tokens(token);

-- Create verification tokens table
CREATE TABLE IF NOT EXISTS auth.verification_tokens (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    token VARCHAR(512) UNIQUE NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    verified BOOLEAN DEFAULT false,
    verified_at TIMESTAMP
);

CREATE INDEX idx_verification_tokens_user_id ON auth.verification_tokens(user_id);
CREATE INDEX idx_verification_tokens_token ON auth.verification_tokens(token);

-- Update timestamp trigger function
CREATE OR REPLACE FUNCTION auth.update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Add trigger to users table
CREATE TRIGGER update_users_updated_at
    BEFORE UPDATE ON auth.users
    FOR EACH ROW
    EXECUTE FUNCTION auth.update_updated_at_column();
