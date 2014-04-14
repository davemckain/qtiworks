-- Schema migration script for upgrading from 1.0-beta5 to 1.0-beta6
--
-- NB: This has been written to work with PostgreSQL and will probably need
-- tweaked slightly to work with other databases.
BEGIN WORK;

-- Drop columns in candidate_session that are no longer required after
-- changing the way session access works
ALTER TABLE candidate_sessions DROP session_token;
ALTER TABLE candidate_sessions DROP exit_url;

-- The 'closed' and 'terminated' booleans in candidate_sessions have been
-- replaced by 'finish_time' and 'termination_time'.
-- (As we haven't recorded this data for existing sessions, we'll set
-- rather artifical times in those cases.)
ALTER TABLE candidate_sessions ADD finish_time timestamp without time zone;
ALTER TABLE candidate_sessions ADD termination_time timestamp without time zone;
UPDATE candidate_sessions SET finish_time = creation_time WHERE closed IS TRUE;
UPDATE candidate_sessions SET termination_time = creation_time WHERE is_terminated IS TRUE;
ALTER TABLE candidate_sessions DROP closed;
ALTER TABLE candidate_sessions DROP is_terminated;

-- Drop the idea of "public" assessments and delivery settings
ALTER TABLE assessments DROP public;
ALTER TABLE delivery_settings DROP public;

-- Fix column names to avoid reserved SQL keywords in common databases (See bug #44)
ALTER TABLE deliveries RENAME open TO opened;
ALTER TABLE candidate_string_response_items RENAME string TO string_data;
ALTER TABLE assessments RENAME type TO assessment_type;
ALTER TABLE assessment_packages RENAME type TO assessment_type;
ALTER TABLE delivery_settings RENAME type TO assessment_type;
ALTER TABLE deliveries RENAME type TO delivery_type;
-- Annoyingly, uid is reserved in Oracle
ALTER TABLE lti_users RENAME uid to id;
ALTER TABLE anonymous_users RENAME uid to id;
ALTER TABLE system_users RENAME uid to id;
ALTER TABLE users RENAME uid to id;
ALTER TABLE candidate_sessions RENAME uid to candidate_uid;

COMMIT WORK;
