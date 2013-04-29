BEGIN WORK;

-- Add support for individual candidate comments
ALTER TABLE item_delivery_settings ADD allow_candidate_comment boolean;
UPDATE item_delivery_settings SET allow_candidate_comment = FALSE;
ALTER TABLE item_delivery_settings ALTER allow_candidate_comment SET NOT NULL;

-- Some changes to event names
UPDATE candidate_events SET test_event_type='EXIT_TEST' WHERE test_event_type='EXIT_MULTI_PART_TEST';

-- Add columns to candidate_session_outcomes
ALTER TABLE candidate_session_outcomes ADD base_type VARCHAR(14);
ALTER TABLE candidate_session_outcomes ADD cardinality VARCHAR(8);
UPDATE candidate_session_outcomes SET base_type = 'STRING';
UPDATE candidate_session_outcomes SET cardinality = 'SINGLE';
ALTER TABLE candidate_session_outcomes ALTER base_type SET NOT NULL;
ALTER TABLE candidate_session_outcomes ALTER cardinality SET NOT NULL;

COMMIT WORK;
