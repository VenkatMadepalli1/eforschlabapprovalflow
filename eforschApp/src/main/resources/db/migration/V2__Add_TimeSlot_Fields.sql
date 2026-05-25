-- Add new columns to inventory_time_slot table for time slot details
ALTER TABLE eforsch.inventory_time_slot ADD COLUMN slot_number INT NULL AFTER time_slot_id;
ALTER TABLE eforsch.inventory_time_slot ADD COLUMN day VARCHAR(20) NULL AFTER slot_number;
ALTER TABLE eforsch.inventory_time_slot ADD COLUMN from_time VARCHAR(20) NULL AFTER day;
ALTER TABLE eforsch.inventory_time_slot ADD COLUMN to_time VARCHAR(20) NULL AFTER from_time;
