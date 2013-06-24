BEGIN WORK;

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
CREATE TABLE queued_lti_outcomes(
  qoid BIGINT PRIMARY KEY NOT NULL,
  xid BIGINT NOT NULL REFERENCES candidate_sessions(xid),
  creation_time TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  failure_count INTEGER NOT NULL,
  retry_time TIMESTAMP WITHOUT TIME ZONE,
  score DOUBLE PRECISION NOT NULL
);

-- Add lti_domains table
CREATE TABLE lti_domains(
  ldid BIGINT PRIMARY KEY NOT NULL,
  creation_time TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  disabled BOOLEAN NOT NULL,
  lti_consumer_key VARCHAR(256) UNIQUE NOT NULL,
  lti_consumer_secret VARCHAR(32) NOT NULL
);

COMMIT WORK;
