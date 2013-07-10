BEGIN WORK;

-- This should have been added earlier.
CREATE SEQUENCE queued_lti_outcome_sequence START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;

-- Need to make LTI outcomes column a bit bigger
ALTER TABLE candidate_sessions ALTER lis_reporting_status SET DATA TYPE VARCHAR(24);

COMMIT WORK;
