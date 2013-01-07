-- Trims data from QTIWorks DEV database for migration into production DB
-- on release of 1.0-M2.
--
-- Basically, we're only keeping Sue's assessments, packages and deliveries.
-- All anonymous and candidate data will be removed

--------------------------------------------------------------------------------

-- Delete candidate data
DELETE FROM candidate_file_submissions;
DELETE FROM candidate_string_response_items;
DELETE FROM candidate_responses;
DELETE FROM candidate_attempts;
DELETE FROM candidate_event_notifications;
DELETE FROM candidate_events;
DELETE FROM candidate_session_outcomes;
DELETE FROM candidate_sessions;

-- Drop LTI candidates
DELETE FROM lti_users;
DELETE FROM users where user_type='LTI';

-- Drop anonymous data
DELETE FROM deliveries WHERE aid IN (SELECT aid FROM assessments WHERE owner_uid IN (SELECT uid FROM users WHERE user_type='ANONYMOUS'));
DELETE FROM delivery_settings WHERE owner_uid IN (SELECT uid FROM users WHERE user_type='ANONYMOUS');
DELETE FROM assessment_package_qti_files WHERE apid IN (SELECT apid FROM assessment_packages WHERE aid IN (SELECT aid FROM assessments WHERE owner_uid IN (SELECT uid FROM users WHERE user_type='ANONYMOUS')));
DELETE FROM assessment_package_safe_files WHERE apid IN (SELECT apid FROM assessment_packages WHERE aid IN (SELECT aid FROM assessments WHERE owner_uid IN (SELECT uid FROM users WHERE user_type='ANONYMOUS')));
DELETE FROM assessment_packages WHERE aid IN (SELECT aid FROM assessments WHERE owner_uid IN (SELECT uid FROM users WHERE user_type='ANONYMOUS'));
DELETE FROM assessments WHERE owner_uid IN (SELECT uid FROM users WHERE user_type='ANONYMOUS');
DELETE FROM anonymous_users;
DELETE FROM users WHERE user_type='ANONYMOUS';

-- Delete all non-Sue data (UID=4)
DELETE FROM deliveries WHERE aid IN (SELECT aid FROM assessments WHERE owner_uid <> 4);
DELETE FROM assessment_package_qti_files WHERE apid IN (SELECT apid FROM assessment_packages WHERE aid IN (SELECT aid FROM assessments WHERE owner_uid <> 4));
DELETE FROM assessment_package_safe_files WHERE apid IN (SELECT apid FROM assessment_packages WHERE aid IN (SELECT aid FROM assessments WHERE owner_uid <> 4));
DELETE FROM assessment_packages WHERE aid IN (SELECT aid FROM assessments WHERE owner_uid <> 4);
DELETE FROM assessments WHERE owner_uid <> 4;
DELETE FROM item_delivery_settings WHERE dsid IN (select dsid FROM delivery_settings WHERE owner_uid <> 4);
DELETE FROM test_delivery_settings WHERE dsid IN (select dsid FROM delivery_settings WHERE owner_uid <> 4);
DELETE FROM delivery_settings WHERE owner_uid <> 4;
DELETE FROM instructor_users WHERE uid <> 4;
DELETE FROM users WHERE uid <> 4;

-- Delete transient deliveries
DELETE FROM deliveries WHERE type='USER_TRANSIENT';

-- Delete Sue's unused delivery settings
DELETE FROM item_delivery_settings WHERE dsid=15;
DELETE FROM delivery_settings WHERE dsid=15;

-- Marry up delivery settings IDs so that we can safely merge with production data
ALTER TABLE deliveries DROP CONSTRAINT fk403dbf925362d03b;
ALTER TABLE item_delivery_settings DROP CONSTRAINT fkef11e9e25362d03b;
ALTER TABLE test_delivery_settings DROP CONSTRAINT fk445cf1a15362d03b;
UPDATE item_delivery_settings SET dsid=14 WHERE dsid=16;
UPDATE test_delivery_settings SET dsid=17 WHERE dsid=14;
UPDATE delivery_settings SET dsid=17 WHERE dsid=14;
UPDATE delivery_settings SET dsid=14 WHERE dsid=16;
UPDATE deliveries SET dsid=17 WHERE dsid=14;
UPDATE deliveries SET dsid=14 WHERE dsid=16;
ALTER TABLE ONLY deliveries ADD CONSTRAINT fk403dbf925362d03b FOREIGN KEY (dsid) REFERENCES delivery_settings(dsid);
ALTER TABLE ONLY item_delivery_settings ADD CONSTRAINT fkef11e9e25362d03b FOREIGN KEY (dsid) REFERENCES delivery_settings(dsid);
ALTER TABLE ONLY test_delivery_settings ADD CONSTRAINT fk445cf1a15362d03b FOREIGN KEY (dsid) REFERENCES delivery_settings(dsid);

-- Add to AID/APIDs to avoid number clashes
ALTER TABLE deliveries DROP CONSTRAINT fk403dbf921f06198;
ALTER TABLE assessment_package_safe_files DROP CONSTRAINT assessment_package_safe_files_apid_fkey;
ALTER TABLE assessment_package_qti_files DROP CONSTRAINT assessment_package_qti_files_apid_fkey;
ALTER TABLE assessment_packages DROP CONSTRAINT fkbeb7c9ca1f06198;
UPDATE assessments SET aid=aid+2200;
UPDATE deliveries SET aid=aid+2200;
UPDATE assessment_packages SET aid=aid+2200;
UPDATE assessment_packages SET apid=apid+2600;
UPDATE assessment_package_safe_files SET apid=apid+2600;
UPDATE assessment_package_qti_files SET apid=apid+2600;
ALTER TABLE ONLY deliveries ADD CONSTRAINT fk403dbf921f06198 FOREIGN KEY (aid) REFERENCES assessments(aid);
ALTER TABLE ONLY assessment_package_safe_files ADD CONSTRAINT assessment_package_safe_files_apid_fkey FOREIGN KEY (apid) REFERENCES assessment_packages(apid);
ALTER TABLE ONLY assessment_package_qti_files ADD CONSTRAINT assessment_package_qti_files_apid_fkey FOREIGN KEY (apid) REFERENCES assessment_packages(apid);
ALTER TABLE ONLY assessment_packages ADD CONSTRAINT fkbeb7c9ca1f06198 FOREIGN KEY (aid) REFERENCES assessments(aid);
