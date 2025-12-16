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

-- Create installable_apps table
CREATE TABLE IF NOT EXISTS installable_apps (
    id BigSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    command VARCHAR(1000) NOT NULL,
    description VARCHAR(1000),
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW()
);

-- Initial population of installable_apps
INSERT INTO installable_apps (name, command, description) VALUES
-- SNAP Packages
('code', 'snap install code --classic', 'Visual Studio Code'),
('slack', 'snap install slack', 'Slack (mensajería)'),
('discord', 'snap install discord', 'Discord'),
('postman', 'snap install postman', 'Postman (API testing)'),
('spotify', 'snap install spotify', 'Spotify'),
('vlc', 'snap install vlc', 'VLC Media Player'),
('gimp', 'snap install gimp', 'GIMP (editor de imágenes)'),
('inkscape', 'snap install inkscape', 'Inkscape (diseño vectorial)'),
('blender', 'snap install blender --classic', 'Blender (3D)'),
('obs-studio', 'snap install obs-studio', 'OBS Studio (streaming)'),
('zoom-client', 'snap install zoom-client', 'Zoom'),
('telegram-desktop', 'snap install telegram-desktop', 'Telegram'),
('chromium', 'snap install chromium', 'Chromium Browser'),
('firefox', 'snap install firefox', 'Firefox'),
('brave', 'snap install brave', 'Brave Browser'),
('intellij-idea-community', 'snap install intellij-idea-community --classic', 'IntelliJ IDEA'),
('pycharm-community', 'snap install pycharm-community --classic', 'PyCharm'),
('rider', 'snap install rider --classic', 'JetBrains Rider'),
('webstorm', 'snap install webstorm --classic', 'WebStorm'),
('phpstorm', 'snap install phpstorm --classic', 'PHPStorm'),
('datagrip', 'snap install datagrip --classic', 'DataGrip (DB)'),
('dbeaver-ce', 'snap install dbeaver-ce', 'DBeaver (DB)'),
('insomnia', 'snap install insomnia', 'Insomnia (API)'),
('figma-linux', 'snap install figma-linux', 'Figma'),
('notion-snap', 'snap install notion-snap', 'Notion'),
('drawio', 'snap install drawio', 'Draw.io (diagramas)'),

-- APT Packages
('git', 'apt-get install -y git', 'Git'),
('docker.io', 'apt-get install -y docker.io', 'Docker'),
('nodejs', 'apt-get install -y nodejs', 'Node.js'),
('npm', 'apt-get install -y npm', 'NPM'),
('python3', 'apt-get install -y python3', 'Python 3'),
('python3-pip', 'apt-get install -y python3-pip', 'PIP (Python)'),
('openjdk-17-jdk', 'apt-get install -y openjdk-17-jdk', 'Java 17'),
('openjdk-11-jdk', 'apt-get install -y openjdk-11-jdk', 'Java 11'),
('maven', 'apt-get install -y maven', 'Maven'),
('gradle', 'apt-get install -y gradle', 'Gradle'),
('mysql-server', 'apt-get install -y mysql-server', 'MySQL Server'),
('postgresql', 'apt-get install -y postgresql', 'PostgreSQL'),
('mongodb', 'apt-get install -y mongodb', 'MongoDB'),
('redis-server', 'apt-get install -y redis-server', 'Redis'),
('nginx', 'apt-get install -y nginx', 'Nginx'),
('apache2', 'apt-get install -y apache2', 'Apache'),
('curl', 'apt-get install -y curl', 'cURL'),
('wget', 'apt-get install -y wget', 'Wget'),
('vim', 'apt-get install -y vim', 'Vim'),
('nano', 'apt-get install -y nano', 'Nano'),
('htop', 'apt-get install -y htop', 'htop (monitor)'),
('neofetch', 'apt-get install -y neofetch', 'Neofetch'),
('build-essential', 'apt-get install -y build-essential', 'Compiladores C/C++'),
('gcc', 'apt-get install -y gcc', 'GCC'),
('g++', 'apt-get install -y g++', 'G++'),
('make', 'apt-get install -y make', 'Make'),
('cmake', 'apt-get install -y cmake', 'CMake'),
('php', 'apt-get install -y php', 'PHP'),
('composer', 'apt-get install -y composer', 'Composer (PHP)'),
('ruby', 'apt-get install -y ruby', 'Ruby'),
('golang-go', 'apt-get install -y golang-go', 'Go'),
('rust', 'apt-get install -y rustc', 'Rust'),
('dotnet-sdk-8.0', 'apt-get install -y dotnet-sdk-8.0', '.NET SDK 8'),
('filezilla', 'apt-get install -y filezilla', 'FileZilla (FTP)'),
('wireshark', 'apt-get install -y wireshark', 'Wireshark'),
('nmap', 'apt-get install -y nmap', 'Nmap'),
('net-tools', 'apt-get install -y net-tools', 'Net Tools'),
('openssh-server', 'apt-get install -y openssh-server', 'SSH Server'),
('tree', 'apt-get install -y tree', 'Tree'),
('unzip', 'apt-get install -y unzip', 'Unzip'),
('zip', 'apt-get install -y zip', 'Zip')
ON CONFLICT (command) DO NOTHING;

-- Create computers table for monitoring overlay
CREATE TABLE IF NOT EXISTS computers (
    id BigSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    ip_address VARCHAR(50),
    mac_address VARCHAR(50),
    status VARCHAR(50) NOT NULL DEFAULT 'OFFLINE',
    last_seen TIMESTAMP,
    lab_name VARCHAR(100),
    room_number INTEGER,
    position_in_room VARCHAR(50),
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITHOUT TIME ZONE
);




