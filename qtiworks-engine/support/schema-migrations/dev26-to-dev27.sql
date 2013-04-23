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

-- Moved 'allow result' & 'allow source' to general delivery settings
ALTER TABLE delivery_settings ADD allow_source boolean;
ALTER TABLE delivery_settings ADD allow_result boolean;
UPDATE delivery_settings ds SET allow_source = ids.allow_source FROM item_delivery_settings ids WHERE ids.dsid = ds.dsid;
UPDATE delivery_settings ds SET allow_result = ids.allow_result FROM item_delivery_settings ids WHERE ids.dsid = ds.dsid;
ALTER TABLE delivery_settings ALTER allow_source SET NOT NULL;
ALTER TABLE delivery_settings ALTER allow_result SET NOT NULL;
ALTER TABLE item_delivery_settings DROP allow_source;
ALTER TABLE item_delivery_settings DROP allow_result;

-- Some changes to event names
UPDATE candidate_events SET test_event_type='EXIT_TEST' WHERE test_event_type='EXIT_MULTI_PART_TEST';

COMMIT WORK;
