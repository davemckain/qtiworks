BEGIN WORK;

-- Add support for individual candidate comments
ALTER TABLE item_delivery_settings ADD allow_candidate_comment boolean;
UPDATE item_delivery_settings SET allow_candidate_comment = FALSE;
ALTER TABLE item_delivery_settings ALTER allow_candidate_comment SET NOT NULL;

-- Moved prompt to item_delivery_settings
ALTER TABLE item_delivery_settings ADD prompt text;
UPDATE item_delivery_settings ids SET prompt = ds.prompt FROM delivery_settings ds WHERE ds.dsid = ids.dsid;
ALTER TABLE item_delivery_settings ALTER prompt SET NOT NULL;
ALTER TABLE delivery_settings DROP prompt;

-- Some changes to event names
UPDATE candidate_events SET test_event_type='EXIT_TEST' WHERE test_event_type='EXIT_MULTI_PART_TEST';

COMMIT WORK;
