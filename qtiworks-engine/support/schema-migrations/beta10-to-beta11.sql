-- Schema update preparation script for migrating from
-- QTIWorks Engine 1.0-beta10 to 1.0-beta11.
--
-- This script is written for PostgreSQL only. Due to issue #72,
-- nobody must have been using MySQL so this doesn't matter!
--
-- How to apply this update:
--
-- (1) Run the PostgreSQL client utility (psql) on your QTIWorks
--     database.
--
-- (2) Invoke:
--     \i /path/to/beta10-to-beta11.sql
--
-- (3) Then run the *updateSchema* action in the QTIWorks engine
--     manager to complete the schema update.
--
-- ************************************************************

-- Delete duplicate constraint
ALTER TABLE lti_nonces DROP CONSTRAINT IF EXISTS lti_nonces_consumer_key_key;

-- Resize some LTI columns (see issue #72)
ALTER TABLE deliveries ALTER COLUMN lti_consumer_key_token TYPE varchar(128);
ALTER TABLE lti_contexts ALTER COLUMN context_id TYPE varchar(128);
ALTER TABLE lti_contexts ALTER COLUMN context_label TYPE varchar(128);
ALTER TABLE lti_contexts ALTER COLUMN fallback_resource_link_id TYPE varchar(128);
ALTER TABLE lti_domains ALTER COLUMN consumer_key TYPE varchar(128);
ALTER TABLE lti_nonces ALTER COLUMN consumer_key TYPE varchar(128);
ALTER TABLE lti_nonces ALTER COLUMN nonce TYPE varchar(128);
ALTER TABLE lti_resources ALTER COLUMN resource_link_id TYPE varchar(128);
ALTER TABLE lti_resources ALTER COLUMN tool_consumer_info_product_family_code TYPE varchar(128);
ALTER TABLE lti_resources ALTER COLUMN tool_consumer_info_version TYPE varchar(128);
ALTER TABLE lti_users ALTER COLUMN lti_user_id TYPE varchar(128);
ALTER TABLE lti_users ALTER COLUMN logical_key TYPE varchar(192);

-- Delete foreign keys. They will be recreated by Hibernate
-- using the new naming scheme in Hibernate 5.x when you run
-- the *updateSchema* action in the QTIWorks Engine Manager.
DO LANGUAGE plpgsql
$$
DECLARE r record;
BEGIN
  FOR r IN SELECT table_name, constraint_name
    FROM information_schema.table_constraints
    WHERE table_schema = current_schema
      AND table_catalog = current_catalog
      AND constraint_type = 'FOREIGN KEY'
  LOOP
    RAISE INFO 'Dropping foreign key constraint % for recreation', r.constraint_name;
    EXECUTE 'ALTER TABLE ' || quote_ident(r.table_name) || ' DROP CONSTRAINT ' || quote_ident(r.constraint_name);
  END LOOP;
  RAISE INFO 'Please now run the updateSchema action in the QTIWorks Engine Manager!';
END
$$;
