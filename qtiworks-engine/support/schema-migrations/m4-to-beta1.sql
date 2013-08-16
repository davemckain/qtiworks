-- Schema migration script for upgrading from 1.0-M4 to 1.0-M5.
-- (This is not complete yet. It incorporates a merger of DEV26->DEV32 at present)
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

-- The template_processing_limit column should be optional
ALTER TABLE delivery_settings ALTER template_processing_limit DROP NOT NULL;
UPDATE delivery_settings SET template_processing_limit = NULL WHERE template_processing_limit <= 0;

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

-- Some changes to item and test candidate event names
ALTER TABLE candidate_events ALTER item_event_type SET DATA TYPE VARCHAR(32);
ALTER TABLE candidate_events ALTER test_event_type SET DATA TYPE VARCHAR(32);
UPDATE candidate_events SET item_event_type='ENTER' WHERE item_event_type='INIT';
UPDATE candidate_events SET item_event_type='END' WHERE item_event_type='CLOSE';
UPDATE candidate_events SET item_event_type='EXIT' WHERE item_event_type='TERMINATE';
UPDATE candidate_events SET test_event_type='ENTER_TEST' WHERE test_event_type='INIT';
UPDATE candidate_events SET test_event_type='ADVANCE_TEST_PART' WHERE test_event_type='EXIT_TEST_PART';
UPDATE candidate_events SET test_event_type='EXIT_TEST' WHERE test_event_type='EXIT_MULTI_PART_TEST';

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

-- Move 'author_mode' flag from delivery_settings to candidate_sessions
ALTER TABLE delivery_settings DROP author_mode;
ALTER TABLE candidate_sessions ADD author_mode BOOLEAN;
UPDATE candidate_sessions SET author_mode = FALSE;
ALTER TABLE candidate_sessions ALTER author_mode SET NOT NULL;

-- Changes to base users table
ALTER TABLE users ADD user_role VARCHAR(10);
UPDATE users SET user_role=user_type;
UPDATE users SET user_type='ANONYMOUS' where user_role='ANONYMOUS';
UPDATE users SET user_type='SYSTEM' where user_role='INSTRUCTOR';
UPDATE users SET user_type='LTI', user_role='CANDIDATE' where user_role='LTI';
ALTER TABLE users ALTER user_role SET NOT NULL;

-- Rename of instructor_users table
ALTER TABLE instructor_users RENAME TO system_users;

-- Add lti_domains table
CREATE TABLE lti_domains (
  ldid BIGINT PRIMARY KEY NOT NULL,
  creation_time TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  disabled BOOLEAN NOT NULL,
  consumer_key VARCHAR(256) UNIQUE NOT NULL,
  consumer_secret VARCHAR(32) NOT NULL
);
CREATE SEQUENCE lti_domain_sequence START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;

-- Link lti_users to lti_domains and deliveries
ALTER TABLE lti_users ADD lti_launch_type VARCHAR(6);
UPDATE lti_users SET lti_launch_type = 'LINK';
ALTER TABLE lti_users ALTER lti_launch_type SET NOT NULL;
ALTER TABLE lti_users ADD did BIGINT REFERENCES deliveries(did);
ALTER TABLE lti_users ADD ldid BIGINT REFERENCES lti_domains(ldid);
ALTER TABLE lti_users ALTER logical_key SET DATA TYPE VARCHAR(300);

-- Add lti_contexts table and sequence
CREATE TABLE lti_contexts (
  lcid BIGINT PRIMARY KEY NOT NULL,
  creation_time TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  ldid BIGINT NOT NULL REFERENCES lti_domains(ldid),
  context_id VARCHAR(256),
  context_label VARCHAR(256),
  context_title TEXT,
  fallback_resource_link_id VARCHAR(256)
);
CREATE SEQUENCE lti_context_sequence START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;

-- Add lti_resources table and sequence
CREATE TABLE lti_resources (
  lrid BIGINT PRIMARY KEY NOT NULL,
  creation_time TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  creator_uid BIGINT NOT NULL REFERENCES users(uid),
  lcid BIGINT NOT NULL REFERENCES lti_contexts(lcid),
  did BIGINT REFERENCES deliveries(did),
  resource_link_id VARCHAR(256) NOT NULL,
  resource_link_title TEXT,
  resource_link_description TEXT,
  tool_consumer_info_product_family_code VARCHAR(256),
  tool_consumer_info_version VARCHAR(256),
  tool_consumer_instance_name TEXT,
  tool_consumer_instance_description TEXT
);
CREATE SEQUENCE lti_resource_sequence START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;

-- Change deliveries table to allow lazy association to assessments & delivery_settings
ALTER TABLE deliveries ALTER aid DROP NOT NULL;
ALTER TABLE deliveries ALTER dsid DROP NOT NULL;
DELETE FROM item_delivery_settings WHERE dsid IN (SELECT dsid FROM delivery_settings WHERE owner_uid IN (SELECT uid FROM users WHERE user_type='ANONYMOUS'));
DELETE FROM test_delivery_settings WHERE dsid IN (SELECT dsid FROM delivery_settings WHERE owner_uid IN (SELECT uid FROM users WHERE user_type='ANONYMOUS'));
DELETE FROM delivery_settings WHERE owner_uid IN (SELECT uid FROM users WHERE user_type='ANONYMOUS');

-- We never really used default delivery settings for assessments
ALTER TABLE assessments DROP default_dsid;

-- Add references to lti_contexts table to assessments & delivery_settings
ALTER TABLE assessments ADD owner_lcid BIGINT REFERENCES lti_contexts(lcid);
ALTER TABLE delivery_settings ADD owner_lcid BIGINT REFERENCES lti_contexts(lcid);

-- Add LTI outcomes stuff to candidate_sessions table
ALTER TABLE candidate_sessions ADD lis_outcome_service_url TEXT;
ALTER TABLE candidate_sessions ADD lis_result_sourcedid TEXT;
ALTER TABLE candidate_sessions ADD lis_score DOUBLE PRECISION;
ALTER TABLE candidate_sessions ADD lis_reporting_status VARCHAR(24);

-- Add LTI outcomes stuff to assessments table
ALTER TABLE assessments ADD lti_result_outcome_identifier TEXT;
ALTER TABLE assessments ADD lti_result_minimum DOUBLE PRECISION;
ALTER TABLE assessments ADD lti_result_maximum DOUBLE PRECISION;

-- Add queued_lti_outcomes table
CREATE TABLE queued_lti_outcomes (
  qoid BIGINT PRIMARY KEY NOT NULL,
  xid BIGINT NOT NULL REFERENCES candidate_sessions(xid),
  creation_time TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  failure_count INTEGER NOT NULL,
  retry_time TIMESTAMP WITHOUT TIME ZONE,
  score DOUBLE PRECISION NOT NULL
);
CREATE SEQUENCE queued_lti_outcome_sequence START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;

-- Add lock_version columns to other tables for consistency
ALTER TABLE deliveries ADD lock_version BIGINT;
UPDATE deliveries SET lock_version = 1;
ALTER TABLE deliveries ALTER lock_version SET NOT NULL;
ALTER TABLE delivery_settings ADD lock_version BIGINT;
UPDATE delivery_settings SET lock_version = 1;
ALTER TABLE delivery_settings ALTER lock_version SET NOT NULL;

-- Simplification to assessment naming
ALTER TABLE assessment_packages ADD file_name VARCHAR(64);
ALTER TABLE assessment_packages ADD title VARCHAR(256);
UPDATE assessment_packages ap SET
  title=(SELECT a.title FROM assessments a WHERE ap.apid=a.selected_apid),
  file_name=(SELECT a.name FROM assessments a WHERE ap.apid=a.selected_apid);
UPDATE assessment_packages SET title='Title' WHERE title IS NULL; -- (This package will end up being deleted)
UPDATE assessment_packages SET file_name='file_name' WHERE file_name IS NULL; -- (Ditto)
ALTER TABLE assessment_packages ALTER title SET NOT NULL;
ALTER TABLE assessment_packages ALTER file_name SET NOT NULL;
ALTER TABLE assessments DROP name;
ALTER TABLE assessments DROP title;

-- Finally, we delete ALL candidate session data from the database.
-- (The filesystem site of things is deleted separately later...)
DELETE FROM candidate_session_outcomes;
DELETE FROM candidate_event_notifications;
DELETE FROM candidate_string_response_items;
DELETE FROM candidate_responses;
DELETE FROM candidate_file_submissions;
DELETE FROM candidate_events;
DELETE FROM candidate_sessions;

COMMIT WORK;
