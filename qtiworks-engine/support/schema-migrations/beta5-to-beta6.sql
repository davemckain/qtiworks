-- Schema migration script for upgrading from 1.0-beta5 -> 1.0-beta6
--
-- NB: This has been written to work with PostgreSQL and will probably need
-- tweaked slightly to work with other databases.
BEGIN WORK;

-- Drop columns in candidate_session that are no longer required after
-- changing the way session access works
ALTER TABLE candidate_sessions DROP session_token;
ALTER TABLE candidate_sessions DROP exit_url;

COMMIT WORK;
