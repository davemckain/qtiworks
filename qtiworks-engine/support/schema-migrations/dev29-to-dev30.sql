BEGIN WORK;

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
ALTER TABLE lti_users ALTER logical_key SET TYPE VARCHAR(300);

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
  lcid BIGINT NOT NULL REFERENCES lti_contexts(lcid),
  resource_link_id VARCHAR(256) NOT NULL,
  resource_link_title TEXT NOT NULL,
  resource_link_description TEXT NOT NULL
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
ALTER TABLE candidate_sessions ADD reporting_status VARCHAR(22);
UPDATE candidate_sessions SET reporting_status = 'LTI_DISABLED';
ALTER TABLE candidate_sessions ALTER reporting_status SET NOT NULL;

-- Add LTI outcomes stuff to deliveries table
ALTER TABLE deliveries ADD lti_result_outcome_identifier TEXT;
ALTER TABLE deliveries ADD lti_result_minimum DOUBLE PRECISION;
ALTER TABLE deliveries ADD lti_result_maximum DOUBLE PRECISION;

-- Add queued_lti_outcomes table
CREATE TABLE queued_lti_outcomes (
  qoid BIGINT PRIMARY KEY NOT NULL,
  xid BIGINT NOT NULL REFERENCES candidate_sessions(xid),
  creation_time TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  failure_count INTEGER NOT NULL,
  retry_time TIMESTAMP WITHOUT TIME ZONE,
  score DOUBLE PRECISION NOT NULL
);

COMMIT WORK;
