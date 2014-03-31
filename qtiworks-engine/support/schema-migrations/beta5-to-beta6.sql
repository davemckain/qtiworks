-- Schema migration script for upgrading from 1.0-beta5 to 1.0-beta6
--
-- NB: This has been written to work with PostgreSQL and will probably need
-- tweaked slightly to work with other databases.
BEGIN WORK;

-- Drop the idea of "public" assessments and delivery settings
ALTER TABLE assessments DROP public;
ALTER TABLE delivery_settings DROP public;

-- Fix column names to avoid reserved SQL keywords in common databases
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
