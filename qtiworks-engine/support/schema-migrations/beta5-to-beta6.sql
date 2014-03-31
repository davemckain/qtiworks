-- Schema migration script for upgrading from 1.0-beta5 to 1.0-beta6
--
-- NB: This has been written to work with PostgreSQL and will probably need
-- tweaked slightly to work with other databases.
BEGIN WORK;

-- Drop the idea of "public" assessments and delivery settings
ALTER TABLE assessments DROP public;
ALTER TABLE delivery_settings DROP public;


COMMIT WORK;
