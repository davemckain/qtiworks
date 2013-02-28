ALTER TABLE candidate_events DROP playback_xeid;
ALTER TABLE item_delivery_settings DROP allow_playback;
DELETE FROM candidate_events WHERE item_event_type = 'PLAYBACK';
-- Move sequences on to avoid Hibernate auto-generation collisions.
-- (Not sure why this is a problem now and not in DEV24...)
SELECT setval('assessment_package_sequence', nextval('assessment_package_sequence') + 10000);
SELECT setval('assessment_sequence', nextval('assessment_sequence') + 10000);
SELECT setval('candidate_event_notification_sequence', nextval('candidate_event_notification_sequence') + 10000);
SELECT setval('candidate_event_sequence', nextval('candidate_event_sequence') + 10000);
SELECT setval('candidate_file_submission_sequence', nextval('candidate_file_submission_sequence') + 10000);
SELECT setval('candidate_response_sequence', nextval('candidate_response_sequence') + 10000);
SELECT setval('candidate_session_outcome_sequence', nextval('candidate_session_outcome_sequence') + 10000);
SELECT setval('candidate_session_sequence', nextval('candidate_session_sequence') + 10000);
SELECT setval('delivery_sequence', nextval('delivery_sequence') + 10000);
SELECT setval('delivery_settings_sequence', nextval('delivery_settings_sequence') + 10000);
SELECT setval('sample_category_sequence', nextval('sample_category_sequence') + 10000);
SELECT setval('user_sequence', nextval('user_sequence') + 10000);
