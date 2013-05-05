BEGIN WORK;

-- Add support for individual candidate comments
ALTER TABLE item_delivery_settings ADD allow_candidate_comment boolean;
UPDATE item_delivery_settings SET allow_candidate_comment = FALSE;
ALTER TABLE item_delivery_settings ALTER allow_candidate_comment SET NOT NULL;

-- Moved 'prompt' down to item_delivery_settings
ALTER TABLE item_delivery_settings ADD prompt text;
UPDATE item_delivery_settings ids SET prompt = ds.prompt FROM delivery_settings ds WHERE ds.dsid = ids.dsid;
ALTER TABLE delivery_settings DROP prompt;

-- Moved 'allow result' & 'allow source' up to common delivery_settings
ALTER TABLE delivery_settings ADD allow_source boolean;
ALTER TABLE delivery_settings ADD allow_result boolean;
UPDATE delivery_settings ds SET allow_source = ids.allow_source FROM item_delivery_settings ids WHERE ids.dsid = ds.dsid;
UPDATE delivery_settings ds SET allow_result = ids.allow_result FROM item_delivery_settings ids WHERE ids.dsid = ds.dsid;
UPDATE delivery_settings SET allow_source = FALSE where allow_source IS NULL;
UPDATE delivery_settings SET allow_result = FALSE where allow_result IS NULL;
ALTER TABLE delivery_settings ALTER allow_source SET NOT NULL;
ALTER TABLE delivery_settings ALTER allow_result SET NOT NULL;
ALTER TABLE item_delivery_settings DROP allow_source;
ALTER TABLE item_delivery_settings DROP allow_result;

-- Rename some columns in item_delivery_settings...
-- allow_close -> allow_end
ALTER TABLE item_delivery_settings ADD allow_end boolean;
UPDATE item_delivery_settings SET allow_end = allow_close;
ALTER TABLE item_delivery_settings ALTER allow_end SET NOT NULL;
ALTER TABLE item_delivery_settings DROP allow_close;
-- allow_reset_when_interacting -> allow_soft_reset_when_open
ALTER TABLE item_delivery_settings ADD allow_soft_reset_when_open boolean;
UPDATE item_delivery_settings SET allow_soft_reset_when_open = allow_reset_when_interacting;
ALTER TABLE item_delivery_settings ALTER allow_soft_reset_when_open SET NOT NULL;
ALTER TABLE item_delivery_settings DROP allow_reset_when_interacting;
-- allow_reset_when_closed -> allow_soft_reset_when_ended
ALTER TABLE item_delivery_settings ADD allow_soft_reset_when_ended boolean;
UPDATE item_delivery_settings SET allow_soft_reset_when_ended = allow_reset_when_closed;
ALTER TABLE item_delivery_settings ALTER allow_soft_reset_when_ended SET NOT NULL;
ALTER TABLE item_delivery_settings DROP allow_reset_when_closed;
-- allow_reinit_when_interacting -> allow_hard_reset_when_open
ALTER TABLE item_delivery_settings ADD allow_hard_reset_when_open boolean;
UPDATE item_delivery_settings SET allow_hard_reset_when_open = allow_reinit_when_interacting;
ALTER TABLE item_delivery_settings ALTER allow_hard_reset_when_open SET NOT NULL;
ALTER TABLE item_delivery_settings DROP allow_reinit_when_interacting;
-- allow_reinit_when_closed -> allow_hard_reset_when_ended
ALTER TABLE item_delivery_settings ADD allow_hard_reset_when_ended boolean;
UPDATE item_delivery_settings SET allow_hard_reset_when_ended = allow_reinit_when_closed;
ALTER TABLE item_delivery_settings ALTER allow_hard_reset_when_ended SET NOT NULL;
ALTER TABLE item_delivery_settings DROP allow_reinit_when_closed;
-- allow_solution_when_interacting -> allow_solution_when_open
ALTER TABLE item_delivery_settings ADD allow_solution_when_open boolean;
UPDATE item_delivery_settings SET allow_solution_when_open = allow_solution_when_interacting;
ALTER TABLE item_delivery_settings ALTER allow_solution_when_open SET NOT NULL;
ALTER TABLE item_delivery_settings DROP allow_solution_when_interacting;
-- allow_solution_when_closed -> allow_solution_when_ended
ALTER TABLE item_delivery_settings ADD allow_solution_when_ended boolean;
UPDATE item_delivery_settings SET allow_solution_when_ended = allow_solution_when_closed;
ALTER TABLE item_delivery_settings ALTER allow_solution_when_ended SET NOT NULL;
ALTER TABLE item_delivery_settings DROP allow_solution_when_closed;

-- Some changes to event names
UPDATE candidate_events SET test_event_type='EXIT_TEST' WHERE test_event_type='EXIT_MULTI_PART_TEST';
UPDATE candidate_events SET item_event_type='EXIT' WHERE item_event_type='TERMINATE';
UPDATE candidate_events SET item_event_type='END' WHERE item_event_type='CLOSE';
UPDATE candidate_events SET item_event_type='ENTER' WHERE item_event_type='INIT';

-- Add columns to candidate_session_outcomes
ALTER TABLE candidate_session_outcomes ADD base_type VARCHAR(14);
ALTER TABLE candidate_session_outcomes ADD cardinality VARCHAR(8);
UPDATE candidate_session_outcomes SET base_type = 'STRING';
UPDATE candidate_session_outcomes SET cardinality = 'SINGLE';
ALTER TABLE candidate_session_outcomes ALTER cardinality SET NOT NULL;

COMMIT WORK;
