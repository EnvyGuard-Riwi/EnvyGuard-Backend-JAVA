-- V3: Add sala columns to existing commands table

-- Add new columns for sala-based architecture
ALTER TABLE commands ADD COLUMN IF NOT EXISTS sala_number INTEGER;
ALTER TABLE commands ADD COLUMN IF NOT EXISTS pc_id BIGINT;
ALTER TABLE commands ADD COLUMN IF NOT EXISTS user_email VARCHAR(255);

-- Update existing records to have default values (if any exist)
UPDATE commands SET sala_number = 0 WHERE sala_number IS NULL;
UPDATE commands SET pc_id = 0 WHERE pc_id IS NULL;

-- Now make the columns NOT NULL
ALTER TABLE commands ALTER COLUMN sala_number SET NOT NULL;
ALTER TABLE commands ALTER COLUMN pc_id SET NOT NULL;

-- Add indexes for new columns
CREATE INDEX IF NOT EXISTS idx_commands_sala_number ON commands(sala_number);
CREATE INDEX IF NOT EXISTS idx_commands_pc_id ON commands(pc_id);
CREATE INDEX IF NOT EXISTS idx_commands_user_email ON commands(user_email);
