BEGIN WORK;
-- Had to rename candidate_sequence.terminated for MySQL compatibility
ALTER TABLE candidate_sessions ADD column is_terminated boolean;
UPDATE candidate_sessions SET is_terminated = terminated;
ALTER TABLE candidate_sessions ALTER COLUMN is_terminated SET NOT NULL;
ALTER TABLE candidate_sessions DROP COLUMN terminated;
-- Further tweaks to @SequenceGenerator annotations
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
--
COMMIT WORK;
