ALTER TABLE candidate_events DROP playback_xeid;
ALTER TABLE item_delivery_settings DROP allow_playback;
DELETE FROM candidate_events WHERE item_event_type = 'PLAYBACK';
-- The following should have been in M3. We need to keep sequence settings
-- in sync with Java @SequenceGenerator annotations.
ALTER SEQUENCE assessment_package_sequence START WITH 1 INCREMENT BY 10;
ALTER SEQUENCE assessment_sequence START WITH 1 INCREMENT BY 10;
ALTER SEQUENCE candidate_event_notification_sequence START WITH 1 INCREMENT BY 50;
ALTER SEQUENCE candidate_event_sequence START WITH 1 INCREMENT BY 50;
ALTER SEQUENCE candidate_file_submission_sequence START WITH 1 INCREMENT BY 50;
ALTER SEQUENCE candidate_response_sequence START WITH 1 INCREMENT BY 50;
ALTER SEQUENCE candidate_session_outcome_sequence START WITH 1 INCREMENT BY 50;
ALTER SEQUENCE candidate_session_sequence START WITH 1 INCREMENT BY 50;
ALTER SEQUENCE delivery_sequence START WITH 1 INCREMENT BY 5;
ALTER SEQUENCE delivery_settings_sequence START WITH 1 INCREMENT BY 5;
ALTER SEQUENCE sample_category_sequence START WITH 1 INCREMENT BY 10;
ALTER SEQUENCE user_sequence START WITH 1000 INCREMENT BY 10;
