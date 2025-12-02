-- Crear tablas manualmente
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    enabled BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS computers (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    mac_address VARCHAR(255) UNIQUE,
    ip_address VARCHAR(255),
    status VARCHAR(255) NOT NULL DEFAULT 'OFFLINE',
    last_seen TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS commands (
    id BIGSERIAL PRIMARY KEY,
    computer_name VARCHAR(255) NOT NULL,
    command_type VARCHAR(255) NOT NULL,
    parameters TEXT,
    status VARCHAR(255) NOT NULL DEFAULT 'PENDING',
    sent_at TIMESTAMP,
    executed_at TIMESTAMP,
    result_message VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);