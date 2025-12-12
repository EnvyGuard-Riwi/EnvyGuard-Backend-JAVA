-- V1: Create initial database schema

-- Create users table
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    role VARCHAR(50) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create computers table
CREATE TABLE IF NOT EXISTS computers (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    ip_address VARCHAR(255),
    mac_address VARCHAR(255),
    status VARCHAR(50) NOT NULL DEFAULT 'OFFLINE',
    last_seen TIMESTAMP,
    lab_name VARCHAR(255),
    room_number INTEGER,
    position_in_room VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create commands table
CREATE TABLE IF NOT EXISTS commands (
    id BIGSERIAL PRIMARY KEY,
    computer_name VARCHAR(255) NOT NULL,
    target_ip VARCHAR(255),
    mac_address VARCHAR(255),
    action VARCHAR(255) NOT NULL,
    parameters TEXT,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    sent_at TIMESTAMP,
    executed_at TIMESTAMP,
    result_message VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_computers_name ON computers(name);
CREATE INDEX IF NOT EXISTS idx_computers_ip_address ON computers(ip_address);
CREATE INDEX IF NOT EXISTS idx_computers_room_number ON computers(room_number);
CREATE INDEX IF NOT EXISTS idx_computers_status ON computers(status);

CREATE INDEX IF NOT EXISTS idx_commands_computer_name ON commands(computer_name);
CREATE INDEX IF NOT EXISTS idx_commands_status ON commands(status);
CREATE INDEX IF NOT EXISTS idx_commands_created_at ON commands(created_at);

CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_role ON users(role);
