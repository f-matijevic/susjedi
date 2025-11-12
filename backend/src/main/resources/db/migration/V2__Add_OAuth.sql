ALTER TABLE users
    ADD COLUMN oauth_provider VARCHAR(20),
ADD COLUMN oauth_provider_id VARCHAR(100),
ADD COLUMN last_login TIMESTAMP;

ALTER TABLE users ALTER COLUMN username DROP NOT NULL;
ALTER TABLE users ALTER COLUMN password DROP NOT NULL;

CREATE INDEX idx_users_oauth ON users(oauth_provider, oauth_provider_id);

ALTER TABLE users ADD CONSTRAINT check_oauth_or_local
    CHECK (
        (oauth_provider IS NOT NULL AND password IS NULL) OR
        (oauth_provider IS NULL AND password IS NOT NULL AND username IS NOT NULL) OR
        (oauth_provider IS NULL AND password IS NULL AND username IS NULL)
        );

COMMENT ON COLUMN users.oauth_provider IS 'OAuth2 provider: GOOGLE, MICROSOFT ili NULL za lokalni račun';
COMMENT ON COLUMN users.oauth_provider_id IS 'Unique ID od OAuth2 providera';