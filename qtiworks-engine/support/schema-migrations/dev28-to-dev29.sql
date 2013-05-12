BEGIN WORK;

-- Remove the 'allow_source' and 'allow_result' columns from 'delivery_settings'.
-- The functinality for this is now controlled by the 'author_mode' column.
ALTER TABLE delivery_settings DROP allow_result;
ALTER TABLE delivery_settings DROP allow_source;

COMMIT WORK;
