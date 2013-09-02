-- Schema migration script for upgrading from 1.0-beta2 -> 1.0-beta3
--
-- NB: This has been written to work with PostgreSQL and will probably need
-- tweaked slightly to work with other databases.
BEGIN WORK;

-- Change the exit_url field to be unlimited text
ALTER TABLE candidate_sessions ALTER exit_url SET DATA TYPE TEXT;

COMMIT WORK;
