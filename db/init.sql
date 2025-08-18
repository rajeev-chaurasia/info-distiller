-- Create the users table
-- We use TIMESTAMPTZ to store timestamps with timezone information, which is a best practice.
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    time_zone VARCHAR(100) DEFAULT 'UTC',
    prefs_json JSONB, -- JSONB is an efficient binary format for storing JSON data in Postgres.
    rss_private_token VARCHAR(255) UNIQUE NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Create the interests table
-- The user_id is a foreign key that references the users table.
-- This ensures that an interest must belong to a valid user (data integrity).
-- ON DELETE CASCADE means if a user is deleted, their interests are also deleted.
CREATE TABLE interests (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    query_template TEXT, -- The query to run against a news API, e.g., '"generative ai" OR "large language models"'
    allowlist_json JSONB, -- e.g., {"sources": ["techcrunch.com", "wired.com"]}
    denylist_json JSONB, -- e.g., {"keywords": ["crypto", "NFT"]}
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- We can add some initial data for testing purposes.
-- This helps in development so we don't start with an empty database.
INSERT INTO users (email, rss_private_token) VALUES ('test@example.com', 'test-token-123');
INSERT INTO interests (user_id, name, query_template) VALUES (1, 'Artificial Intelligence', '"artificial intelligence" OR "vertex ai"');