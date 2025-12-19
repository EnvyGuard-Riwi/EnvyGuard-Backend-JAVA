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

-- Create sala_1 table
CREATE TABLE IF NOT EXISTS sala_1 (
    id BIGSERIAL PRIMARY KEY,
    nombre_pc VARCHAR(50) NOT NULL,
    ip VARCHAR(50),
    mac VARCHAR(50)
);

-- Create sala_2 table
CREATE TABLE IF NOT EXISTS sala_2 (
    id BIGSERIAL PRIMARY KEY,
    nombre_pc VARCHAR(50) NOT NULL,
    ip VARCHAR(50),
    mac VARCHAR(50)
);

-- Create sala_3 table
CREATE TABLE IF NOT EXISTS sala_3 (
    id BIGSERIAL PRIMARY KEY,
    nombre_pc VARCHAR(50) NOT NULL,
    ip VARCHAR(50),
    mac VARCHAR(50)
);

-- Create sala_4 table
CREATE TABLE IF NOT EXISTS sala_4 (
    id BIGSERIAL PRIMARY KEY,
    nombre_pc VARCHAR(50) NOT NULL,
    ip VARCHAR(50),
    mac VARCHAR(50)
);

-- Create commands table
CREATE TABLE IF NOT EXISTS commands (
    id BIGSERIAL PRIMARY KEY,
    sala_number INTEGER NOT NULL,        -- Número de sala (1, 2, 3, 4)
    pc_id BIGINT NOT NULL,               -- ID del PC en la tabla sala_X
    computer_name VARCHAR(255) NOT NULL, -- Nombre del PC (ej: "PC 1")
    target_ip VARCHAR(255),              -- IP del PC objetivo
    mac_address VARCHAR(255),            -- MAC del PC objetivo
    action VARCHAR(255) NOT NULL,        -- Acción: SHUTDOWN, REBOOT, WAKE_ON_LAN, etc
    parameters TEXT,                     -- Parámetros adicionales en JSON
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',  -- PENDING, SENT, EXECUTED, FAILED
    sent_at TIMESTAMP,                   -- Cuándo se envió a RabbitMQ
    executed_at TIMESTAMP,               -- Cuándo se ejecutó en el PC
    result_message VARCHAR(500),         -- Mensaje de resultado del agente
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    user_email VARCHAR(255)              -- Quién ejecutó el comando (opcional)
);

-- Create incidents table for news/issues tracking
CREATE TABLE IF NOT EXISTS incidents (
    id BIGSERIAL PRIMARY KEY,
    description VARCHAR(1000) NOT NULL,
    severity VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP
);


-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_sala_1_nombre_pc ON sala_1(nombre_pc);
CREATE INDEX IF NOT EXISTS idx_sala_1_ip ON sala_1(ip);

CREATE INDEX IF NOT EXISTS idx_sala_2_nombre_pc ON sala_2(nombre_pc);
CREATE INDEX IF NOT EXISTS idx_sala_2_ip ON sala_2(ip);

CREATE INDEX IF NOT EXISTS idx_sala_3_nombre_pc ON sala_3(nombre_pc);
CREATE INDEX IF NOT EXISTS idx_sala_3_ip ON sala_3(ip);

CREATE INDEX IF NOT EXISTS idx_sala_4_nombre_pc ON sala_4(nombre_pc);
CREATE INDEX IF NOT EXISTS idx_sala_4_ip ON sala_4(ip);

CREATE INDEX IF NOT EXISTS idx_commands_sala_number ON commands(sala_number);
CREATE INDEX IF NOT EXISTS idx_commands_pc_id ON commands(pc_id);
CREATE INDEX IF NOT EXISTS idx_commands_computer_name ON commands(computer_name);
CREATE INDEX IF NOT EXISTS idx_commands_status ON commands(status);
CREATE INDEX IF NOT EXISTS idx_commands_created_at ON commands(created_at);
CREATE INDEX IF NOT EXISTS idx_commands_user_email ON commands(user_email);

CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_role ON users(role);

CREATE INDEX IF NOT EXISTS idx_incidents_status ON incidents(status);
CREATE INDEX IF NOT EXISTS idx_incidents_severity ON incidents(severity);
CREATE INDEX IF NOT EXISTS idx_incidents_created_at ON incidents(created_at);

-- Create blocked_websites table
CREATE TABLE IF NOT EXISTS blocked_websites (
    id BigSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    url VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW()
);




