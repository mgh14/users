CREATE SCHEMA IF NOT EXISTS users;

COMMENT ON SCHEMA users IS 'Schema for storing information about users of the application';

CREATE TABLE IF NOT EXISTS users.user_credential (
  user_credential_id SERIAL PRIMARY KEY,
  email VARCHAR (50) UNIQUE NOT NULL,
  password VARCHAR (50) NOT NULL,
  created_by VARCHAR (100) NOT NULL,
  modified_by VARCHAR (100) NOT NULL,
  created_at TIMESTAMPTZ DEFAULT current_timestamp,
  modified_at TIMESTAMPTZ DEFAULT current_timestamp
);

COMMENT ON TABLE users.user_credential IS 'Table for storing identifying user credential information';

-- TODO: comment columns
--COMMENT ON COLUMN users.user_credential.user_credential_id IS '';

CREATE TABLE IF NOT EXISTS users.user_info (
  user_info_id SERIAL PRIMARY KEY,
  user_credential_user_credential_id SERIAL REFERENCES users.user_credential(user_credential_id) NOT NULL,
  external_id UUID NOT NULL,
  first_name VARCHAR (100) NOT NULL,
  last_name VARCHAR (100) NOT NULL,
  created_by VARCHAR (100) NOT NULL,
  modified_by VARCHAR (100) NOT NULL,
  created_at TIMESTAMPTZ DEFAULT current_timestamp,
  modified_at TIMESTAMPTZ DEFAULT current_timestamp
);

COMMENT ON TABLE users.user_info IS 'Stores basic info about a user – SENSITIVE PII';

CREATE TABLE IF NOT EXISTS users.current_token (
  current_token_id SERIAL PRIMARY KEY,
  user_credential_user_credential_id SERIAL REFERENCES users.user_credential(user_credential_id) NOT NULL,
  jwt VARCHAR (1000) NOT NULL,
  expiry BIGINT NOT NULL
);

COMMENT ON TABLE users.current_token IS 'Stores currently-valid tokens for authenticated users';
