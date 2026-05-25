-- Create StorageLocation table
CREATE TABLE IF NOT EXISTS eforsch.storage_location (
    id                INT PRIMARY KEY AUTO_INCREMENT,
    storage_location  VARCHAR(255) NOT NULL,
    created_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create index on storage_location for faster queries
CREATE INDEX idx_storage_location ON eforsch.storage_location(storage_location);
