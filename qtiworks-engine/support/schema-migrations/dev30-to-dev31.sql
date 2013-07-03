BEGIN WORK;

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

COMMIT WORK;
