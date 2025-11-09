-- Initialize EduBridge database schemas

-- Create separate schemas for different services
CREATE SCHEMA IF NOT EXISTS auth;
CREATE SCHEMA IF NOT EXISTS users;
CREATE SCHEMA IF NOT EXISTS content;
CREATE SCHEMA IF NOT EXISTS assessment;
CREATE SCHEMA IF NOT EXISTS analytics;
CREATE SCHEMA IF NOT EXISTS payments;

-- Set search path
SET search_path TO public, auth, users, content, assessment, analytics, payments;

-- Create extensions
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Grant privileges
GRANT ALL PRIVILEGES ON SCHEMA auth TO edubridge;
GRANT ALL PRIVILEGES ON SCHEMA users TO edubridge;
GRANT ALL PRIVILEGES ON SCHEMA content TO edubridge;
GRANT ALL PRIVILEGES ON SCHEMA assessment TO edubridge;
GRANT ALL PRIVILEGES ON SCHEMA analytics TO edubridge;
GRANT ALL PRIVILEGES ON SCHEMA payments TO edubridge;

-- Log initialization
DO $$
BEGIN
    RAISE NOTICE 'EduBridge database initialized successfully';
END $$;
