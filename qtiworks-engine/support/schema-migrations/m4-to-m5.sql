-- Schema migration script for upgrading from 1.0-M4 to 1.0-M5.
-- (This is not complete yet. It incorporates a merger of DEV26->DEV29 at present)
--
-- NB: This has been written to work with PostgreSQL and will probably need
-- tweaked slightly to work with other databases.
BEGIN WORK;

-- Add support for individual candidate comments in item_delivery_settings
ALTER TABLE item_delivery_settings ADD allow_candidate_comment boolean;
UPDATE item_delivery_settings SET allow_candidate_comment = FALSE;
ALTER TABLE item_delivery_settings ALTER allow_candidate_comment SET NOT NULL;

-- Moved 'prompt' down to item_delivery_settings
ALTER TABLE item_delivery_settings ADD prompt text;
UPDATE item_delivery_settings ids SET prompt = ds.prompt FROM delivery_settings ds WHERE ds.dsid = ids.dsid;
ALTER TABLE delivery_settings DROP prompt;

-- Ended up removing 'allow result' & 'allow source' completely in DEV29.
-- (This functionality is now merged into author_mode)
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

-- Turn author_mode back on for all public delivery settings
-- (DEV29 also did this for individual users' defaults, but this is probably
-- risky so I won't do it here.)
UPDATE delivery_settings SET author_mode = true WHERE public IS TRUE;

-- Change to Assessment -> AssessmentPackage relationships.
-- First change is setting selected package to be the newest one for each row in assessments
ALTER TABLE assessments ADD selected_apid bigint REFERENCES assessment_packages(apid);
UPDATE assessments a SET selected_apid = (SELECT MAX(apid) FROM assessment_packages WHERE aid=a.aid);
ALTER TABLE assessments ALTER selected_apid SET NOT NULL;
ALTER TABLE assessment_packages ALTER aid DROP NOT NULL;

-- New columns in assessment_package. We'll set these to defaults for now;
-- an Engine Manager action should be run to fill in the data appropriately afterwards
ALTER TABLE assessment_packages ADD launchable boolean;
ALTER TABLE assessment_packages ADD error_count int;
ALTER TABLE assessment_packages ADD warning_count int;
UPDATE assessment_packages SET launchable=valid, error_count=0, warning_count=0;
ALTER TABLE assessment_packages ALTER launchable SET NOT NULL;
ALTER TABLE assessment_packages ALTER error_count SET NOT NULL;
ALTER TABLE assessment_packages ALTER warning_count SET NOT NULL;

-- Some changes to event names
UPDATE candidate_events SET test_event_type='EXIT_TEST' WHERE test_event_type='EXIT_MULTI_PART_TEST';
UPDATE candidate_events SET item_event_type='EXIT' WHERE item_event_type='TERMINATE';
UPDATE candidate_events SET item_event_type='END' WHERE item_event_type='CLOSE';
UPDATE candidate_events SET item_event_type='ENTER' WHERE item_event_type='INIT';

-- Addition of 'exploded' column to candidate_sessions
ALTER TABLE candidate_sessions ADD exploded boolean;
UPDATE candidate_sessions SET exploded=false;
ALTER TABLE candidate_sessions ALTER exploded SET NOT NULL;

-- Add columns to candidate_session_outcomes
ALTER TABLE candidate_session_outcomes ADD base_type VARCHAR(14);
ALTER TABLE candidate_session_outcomes ADD cardinality VARCHAR(8);
UPDATE candidate_session_outcomes SET base_type = 'STRING';
UPDATE candidate_session_outcomes SET cardinality = 'SINGLE';
ALTER TABLE candidate_session_outcomes ALTER cardinality SET NOT NULL;

COMMIT WORK;
