-- V4: Create sala tables and incidents table

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

CREATE INDEX IF NOT EXISTS idx_incidents_status ON incidents(status);
CREATE INDEX IF NOT EXISTS idx_incidents_severity ON incidents(severity);
CREATE INDEX IF NOT EXISTS idx_incidents_created_at ON incidents(created_at);
