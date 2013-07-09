BEGIN WORK;

-- Tweaks to LIS result recording in candidate_sessions table
ALTER TABLE candidate_sessions ADD lis_score DOUBLE PRECISION;
ALTER TABLE candidate_sessions ADD lis_reporting_status VARCHAR(22);
UPDATE candidate_sessions SET lis_reporting_status = reporting_status;
UPDATE candidate_sessions SET lis_reporting_status = NULL WHERE lis_reporting_status = 'SESSION_NOT_ENDED';
ALTER TABLE candidate_sessions DROP reporting_status;

-- Move LTI outcomes stuff from assessments to deliveries table
-- (This was added in DEV30, but I've decided to move this for DEV31)
ALTER TABLE assessments ADD lti_result_outcome_identifier TEXT;
ALTER TABLE assessments ADD lti_result_minimum DOUBLE PRECISION;
ALTER TABLE assessments ADD lti_result_maximum DOUBLE PRECISION;
ALTER TABLE deliveries DROP lti_result_outcome_identifier;
ALTER TABLE deliveries DROP lti_result_minimum;
ALTER TABLE deliveries DROP lti_result_maximum;

-- Add lock_version columns to other tables for consistency
ALTER TABLE deliveries ADD lock_version BIGINT;
UPDATE deliveries SET lock_version = 1;
ALTER TABLE deliveries ALTER lock_version SET NOT NULL;
ALTER TABLE delivery_settings ADD lock_version BIGINT;
UPDATE delivery_settings SET lock_version = 1;
ALTER TABLE delivery_settings ALTER lock_version SET NOT NULL;

-- The template_processing_limit column should be optional
ALTER TABLE delivery_settings ALTER template_processing_limit DROP NOT NULL;
UPDATE delivery_settings SET template_processing_limit = NULL WHERE template_processing_limit <= 0;

COMMIT WORK;
