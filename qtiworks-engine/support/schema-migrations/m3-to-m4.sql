BEGIN WORK;
-- (Taken from DEV24 -> DEV25 and DEV25 -> DEV26)
ALTER TABLE candidate_events DROP playback_xeid;
ALTER TABLE item_delivery_settings DROP allow_playback;
DELETE FROM candidate_events WHERE item_event_type = 'PLAYBACK';
-- The following should have been in M3. We need to keep sequence settings
-- in sync with Java @SequenceGenerator annotations.
ALTER SEQUENCE assessment_package_sequence START WITH 1 INCREMENT BY 1;
ALTER SEQUENCE assessment_sequence START WITH 1 INCREMENT BY 1;
ALTER SEQUENCE candidate_event_notification_sequence START WITH 1 INCREMENT BY 10;
ALTER SEQUENCE candidate_event_sequence START WITH 1 INCREMENT BY 1;
ALTER SEQUENCE candidate_file_submission_sequence START WITH 1 INCREMENT BY 1;
ALTER SEQUENCE candidate_response_sequence START WITH 1 INCREMENT BY 5;
ALTER SEQUENCE candidate_session_outcome_sequence START WITH 1 INCREMENT BY 10;
ALTER SEQUENCE candidate_session_sequence START WITH 1 INCREMENT BY 1;
ALTER SEQUENCE delivery_sequence START WITH 1 INCREMENT BY 1;
ALTER SEQUENCE delivery_settings_sequence START WITH 1 INCREMENT BY 1;
ALTER SEQUENCE sample_category_sequence START WITH 1 INCREMENT BY 10;
ALTER SEQUENCE user_sequence START WITH 1000 INCREMENT BY 1;

ALTER TABLE candidate_sessions ADD column is_terminated boolean;
UPDATE candidate_sessions SET is_terminated = terminated;
ALTER TABLE candidate_sessions ALTER COLUMN is_terminated SET NOT NULL;
ALTER TABLE candidate_sessions DROP COLUMN terminated;
--
COMMIT WORK;
