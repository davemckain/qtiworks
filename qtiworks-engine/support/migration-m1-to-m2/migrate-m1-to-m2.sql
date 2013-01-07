-- Migration script to take the DB schema from M1 to M2

-- Delete candidate data
DELETE FROM candidate_file_submissions;
DELETE FROM candidate_string_response_items;
DELETE FROM candidate_item_responses;
DELETE FROM candidate_item_attempts;
DELETE FROM candidate_item_events;
DELETE FROM candidate_item_sessions;

-- Drop existing candidate tables
DROP TABLE candidate_string_response_items;
DROP TABLE candidate_item_responses;
DROP TABLE candidate_file_submissions;
DROP TABLE candidate_item_attempts;
DROP TABLE candidate_item_events;
DROP TABLE candidate_item_sessions;
DROP SEQUENCE candidate_file_submission_sequence;
DROP SEQUENCE candidate_item_attempt_sequence;
DROP SEQUENCE candidate_item_response_sequence;
DROP SEQUENCE candidate_item_event_sequence;
DROP SEQUENCE candidate_item_session_sequence;

-- Drop LTI candidates
DELETE FROM lti_users;
DELETE FROM users where user_type='LTI';

-- Drop anonymous data
DELETE FROM item_deliveries WHERE aid IN (SELECT aid FROM assessments WHERE owner_uid IN (SELECT uid FROM users WHERE user_type='ANONYMOUS'));
DELETE FROM item_delivery_settings WHERE owner_uid IN (SELECT uid FROM users WHERE user_type='ANONYMOUS');
DELETE FROM assessment_package_qti_files WHERE aid IN (SELECT apid FROM assessment_packages WHERE aid IN (SELECT aid FROM assessments WHERE owner_uid IN (SELECT uid FROM users WHERE user_type='ANONYMOUS')));
DELETE FROM assessment_package_safe_files WHERE aid IN (SELECT apid FROM assessment_packages WHERE aid IN (SELECT aid FROM assessments WHERE owner_uid IN (SELECT uid FROM users WHERE user_type='ANONYMOUS')));
DELETE FROM assessment_packages WHERE aid IN (SELECT aid FROM assessments WHERE owner_uid IN (SELECT uid FROM users WHERE user_type='ANONYMOUS'));
DELETE FROM assessments WHERE owner_uid IN (SELECT uid FROM users WHERE user_type='ANONYMOUS');
DELETE FROM anonymous_users;
DELETE FROM users WHERE user_type='ANONYMOUS';

-- Modify existing tables
ALTER TABLE users ADD email_address VARCHAR(128);
ALTER TABLE users ADD first_name VARCHAR(256);
ALTER TABLE users ADD last_name VARCHAR(256);
UPDATE users u SET first_name=(SELECT first_name FROM instructor_users WHERE uid=u.uid);
UPDATE users u SET last_name=(SELECT last_name FROM instructor_users WHERE uid=u.uid);
UPDATE users u SET email_address=(SELECT email_address FROM instructor_users WHERE uid=u.uid);
ALTER TABLE users ALTER first_name SET NOT NULL;
ALTER TABLE users ALTER last_name SET NOT NULL;
ALTER TABLE users ALTER login_disabled SET NOT NULL;
ALTER TABLE instructor_users DROP first_name;
ALTER TABLE instructor_users DROP last_name;
ALTER TABLE instructor_users DROP email_address;
ALTER TABLE instructor_users DROP login_disabled;
ALTER TABLE lti_users DROP lis_contact_email_primary;
ALTER TABLE lti_users DROP lis_family_name;
ALTER TABLE lti_users DROP lis_given_name;
ALTER TABLE assessment_package_qti_files ADD apid bigint;
UPDATE assessment_package_qti_files SET apid=aid;
ALTER TABLE assessment_package_qti_files ALTER apid SET NOT NULL;
ALTER TABLE assessment_package_qti_files DROP aid;
ALTER TABLE assessment_package_qti_files ADD CONSTRAINT assessment_package_qti_files_apid_fkey FOREIGN KEY (apid) REFERENCES assessment_packages(apid);
ALTER TABLE assessment_package_safe_files ADD apid bigint;
UPDATE assessment_package_safe_files SET apid=aid;
ALTER TABLE assessment_package_safe_files ALTER apid SET NOT NULL;
ALTER TABLE assessment_package_safe_files DROP aid;
ALTER TABLE assessment_package_safe_files ADD CONSTRAINT assessment_package_safe_files_apid_fkey FOREIGN KEY (apid) REFERENCES assessment_packages(apid);
DROP SEQUENCE item_delivery_sequence;
DROP SEQUENCE item_delivery_settings_sequence;
CREATE SEQUENCE delivery_sequence START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
CREATE SEQUENCE delivery_settings_sequence START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
CREATE TABLE deliveries (
    did bigint NOT NULL,
    creation_time timestamp without time zone NOT NULL,
    type character varying(15) NOT NULL,
    lti_consumer_key_token character varying(32),
    lti_consumer_secret character varying(32),
    lti_enabled boolean NOT NULL,
    open boolean NOT NULL,
    title text NOT NULL,
    aid bigint NOT NULL,
    dsid bigint NOT NULL
);
INSERT INTO deliveries
  SELECT did, creation_time, type, lti_consumer_key_token, lti_consumer_secret, lti_enabled, open, title, aid, dsid
  FROM item_deliveries;
DROP TABLE item_deliveries;
ALTER TABLE ONLY deliveries ADD CONSTRAINT deliveries_pkey PRIMARY KEY (did);
CREATE TABLE delivery_settings (
    dsid bigint NOT NULL,
    type character varying(15) NOT NULL,
    author_mode boolean NOT NULL,
    creation_time timestamp without time zone NOT NULL,
    public boolean NOT NULL,
    prompt text,
    template_processing_limit integer NOT NULL,
    title text NOT NULL,
    owner_uid bigint NOT NULL
);
INSERT INTO delivery_settings (dsid, type, author_mode, creation_time, public, prompt, template_processing_limit, title, owner_uid)
  SELECT dsid, 'ASSESSMENT_ITEM', author_mode, creation_time, public, prompt, 0, title, owner_uid
  FROM item_delivery_settings ids;
ALTER TABLE ONLY delivery_settings ADD CONSTRAINT delivery_settings_pkey PRIMARY KEY (dsid);
ALTER TABLE ONLY delivery_settings ADD CONSTRAINT fk2661040e8988ada9 FOREIGN KEY (owner_uid) REFERENCES users(uid);
ALTER TABLE item_delivery_settings DROP author_mode;
ALTER TABLE item_delivery_settings DROP creation_time;
ALTER TABLE item_delivery_settings DROP public;
ALTER TABLE item_delivery_settings DROP prompt;
ALTER TABLE item_delivery_settings DROP title;
ALTER TABLE item_delivery_settings DROP owner_uid;
CREATE TABLE test_delivery_settings (
    dsid bigint NOT NULL
);
ALTER TABLE ONLY item_delivery_settings ADD CONSTRAINT fkef11e9e25362d03b FOREIGN KEY (dsid) REFERENCES delivery_settings(dsid);
ALTER TABLE ONLY test_delivery_settings ADD CONSTRAINT fk445cf1a15362d03b FOREIGN KEY (dsid) REFERENCES delivery_settings(dsid);
ALTER TABLE ONLY deliveries ADD CONSTRAINT fk403dbf921f06198 FOREIGN KEY (aid) REFERENCES assessments(aid);
ALTER TABLE ONLY deliveries ADD CONSTRAINT fk403dbf925362d03b FOREIGN KEY (dsid) REFERENCES delivery_settings(dsid);
ALTER TABLE ONLY assessments DROP CONSTRAINT fk4c13a1f19cd90fac;
ALTER TABLE ONLY assessments ADD CONSTRAINT fk4c13a1f12c6a7379 FOREIGN KEY (default_dsid) REFERENCES delivery_settings(dsid);

-- Create new candidate tables
CREATE SEQUENCE candidate_session_sequence START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
CREATE TABLE candidate_sessions (
    xid bigint NOT NULL,
    closed boolean NOT NULL,
    creation_time timestamp without time zone NOT NULL,
    exit_url character varying(128),
    session_token character varying(32) NOT NULL,
    terminated boolean NOT NULL,
    uid bigint NOT NULL,
    did bigint NOT NULL
);
ALTER TABLE ONLY candidate_sessions ADD CONSTRAINT candidate_sessions_pkey PRIMARY KEY (xid);
ALTER TABLE ONLY candidate_sessions ADD CONSTRAINT fk865fefd98345f08d FOREIGN KEY (did) REFERENCES deliveries(did);
ALTER TABLE ONLY candidate_sessions ADD CONSTRAINT fk865fefd988781d35 FOREIGN KEY (uid) REFERENCES users(uid);
CREATE SEQUENCE candidate_event_sequence START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
CREATE TABLE candidate_events (
    xeid bigint NOT NULL,
    event_category character varying(16) NOT NULL,
    item_event_type character varying(16),
    test_event_type character varying(16),
    test_item_key character varying(74),
    "timestamp" timestamp without time zone NOT NULL,
    xid bigint NOT NULL,
    playback_xeid bigint
);
ALTER TABLE ONLY candidate_events ADD CONSTRAINT candidate_events_pkey PRIMARY KEY (xeid);
ALTER TABLE ONLY candidate_events ADD CONSTRAINT fke05e9f552000cbdd FOREIGN KEY (playback_xeid) REFERENCES candidate_events(xeid);
ALTER TABLE ONLY candidate_events ADD CONSTRAINT fke05e9f558a444140 FOREIGN KEY (xid) REFERENCES candidate_sessions(xid);
CREATE SEQUENCE candidate_event_notification_sequence START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
CREATE TABLE candidate_event_notifications (
    xnid bigint NOT NULL,
    attr_local_name text,
    attr_ns_uri text,
    column_number integer,
    line_number integer,
    message text NOT NULL,
    node_qti_class_name text,
    notification_level character varying(16) NOT NULL,
    notification_type character varying(16) NOT NULL,
    system_id text,
    xeid bigint NOT NULL
);
ALTER TABLE ONLY candidate_event_notifications ADD CONSTRAINT candidate_event_notifications_pkey PRIMARY KEY (xnid);
ALTER TABLE ONLY candidate_event_notifications ADD CONSTRAINT fk8dc2874756ff5039 FOREIGN KEY (xeid) REFERENCES candidate_events(xeid);

CREATE SEQUENCE candidate_attempt_sequence START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
CREATE TABLE candidate_attempts (
    xaid bigint NOT NULL,
    xeid bigint NOT NULL
);
ALTER TABLE ONLY candidate_attempts ADD CONSTRAINT candidate_attempts_pkey PRIMARY KEY (xaid);
ALTER TABLE ONLY candidate_attempts ADD CONSTRAINT candidate_attempts_xeid_key UNIQUE (xeid);
ALTER TABLE ONLY candidate_attempts ADD CONSTRAINT fk52f9a68256ff5039 FOREIGN KEY (xeid) REFERENCES candidate_events(xeid);
CREATE SEQUENCE candidate_file_submission_sequence START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
CREATE TABLE candidate_file_submissions (
    fid bigint NOT NULL,
    content_type character varying(64) NOT NULL,
    creation_time timestamp without time zone NOT NULL,
    file_name text,
    stored_file_path text NOT NULL,
    xid bigint NOT NULL
);
ALTER TABLE ONLY candidate_file_submissions ADD CONSTRAINT candidate_file_submissions_pkey PRIMARY KEY (fid);
ALTER TABLE ONLY candidate_file_submissions ADD CONSTRAINT fk97f84ba08a444140 FOREIGN KEY (xid) REFERENCES candidate_sessions(xid);
CREATE SEQUENCE candidate_response_sequence START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
CREATE TABLE candidate_responses (
    xrid bigint NOT NULL,
    response_type character varying(6) NOT NULL,
    response_identifier character varying(64) NOT NULL,
    response_legality character varying(7) NOT NULL,
    xaid bigint NOT NULL,
    fid bigint
);
ALTER TABLE ONLY candidate_responses ADD CONSTRAINT candidate_responses_pkey PRIMARY KEY (xrid);
ALTER TABLE ONLY candidate_responses ADD CONSTRAINT fkac8f3c96ebeac7a8 FOREIGN KEY (xaid) REFERENCES candidate_attempts(xaid);
ALTER TABLE ONLY candidate_responses ADD CONSTRAINT fkac8f3c96236026f2 FOREIGN KEY (fid) REFERENCES candidate_file_submissions(fid);
CREATE TABLE candidate_string_response_items (
    xrid bigint NOT NULL,
    string text
);
ALTER TABLE ONLY candidate_string_response_items ADD CONSTRAINT fk8d47b674563b4d9f FOREIGN KEY (xrid) REFERENCES candidate_responses(xrid);
CREATE SEQUENCE candidate_session_outcome_sequence START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
CREATE TABLE candidate_session_outcomes (
    xoid bigint NOT NULL,
    outcome_identifier character varying(64) NOT NULL,
    string_value text,
    xid bigint NOT NULL
);
ALTER TABLE ONLY candidate_session_outcomes ADD CONSTRAINT candidate_session_outcomes_pkey PRIMARY KEY (xoid);
ALTER TABLE ONLY candidate_session_outcomes ADD CONSTRAINT fkb71f3e268a444140 FOREIGN KEY (xid) REFERENCES candidate_sessions(xid);

-- Delete transient deliveries
DELETE FROM deliveries WHERE type='USER_TRANSIENT';

-- Crank some sequences on to avoid clashes with merged data
SELECT setval('assessment_sequence', 3000);
SELECT setval('assessment_package_sequence', 3000);
SELECT setval('delivery_sequence', 2000);
SELECT setval('delivery_settings_sequence', 2000);
