BEGIN WORK;

-- Add support for individual candidate comments
ALTER TABLE item_delivery_settings ADD allow_candidate_comment boolean;
UPDATE item_delivery_settings SET allow_candidate_comment = FALSE;
ALTER TABLE item_delivery_settings ALTER allow_candidate_comment SET NOT NULL;

COMMIT WORK;
