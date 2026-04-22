-- Reference SQL schema for database design documentation.
-- The actual database is managed in Google Cloud SQL.
-- This file is kept in GitHub to document the intended table structure,
-- constraints, and relationships used by the application.



CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE profile (
    user_id UUID PRIMARY KEY,
    user_name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE favorite (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    base_currency VARCHAR(10) NOT NULL,
    target_currency VARCHAR(10) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT favorite_user_currency_unique UNIQUE (user_id, base_currency, target_currency),
    CONSTRAINT favorite_user_id_foreign FOREIGN KEY (user_id) REFERENCES profile(user_id) ON DELETE CASCADE
);

CREATE TABLE history (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    base_currency VARCHAR(10) NOT NULL,
    target_currency VARCHAR(10) NOT NULL,
    -- base_amount DECIMAL(12, 2) NOT NULL,
    -- converted_amount DECIMAL(12, 2) NOT NULL,
    rate DECIMAL(12, 6) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT history_user_id_foreign FOREIGN KEY (user_id) REFERENCES profile(user_id) ON DELETE CASCADE
);

CREATE TABLE alert (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    alert_target DECIMAL(12, 6) NOT NULL,
    direction VARCHAR(10) NOT NULL CHECK (direction IN ('above', 'below')),
    last_checked_rate DECIMAL(12, 6),
    last_checked_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    base_currency VARCHAR(10) NOT NULL,
    target_currency VARCHAR(10) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT alert_user_currency_direction_unique 
    UNIQUE (user_id, base_currency, target_currency, direction)
    CONSTRAINT alert_user_id_foreign FOREIGN KEY (user_id) REFERENCES profile(user_id) ON DELETE CASCADE
);