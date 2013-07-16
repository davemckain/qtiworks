BEGIN WORK;

-- This should have been added earlier.
CREATE SEQUENCE queued_lti_outcome_sequence START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;

-- Need to make LTI outcomes column a bit bigger
ALTER TABLE candidate_sessions ALTER lis_reporting_status SET DATA TYPE VARCHAR(24);

-- Simplification to assessment naming
ALTER TABLE assessment_packages ADD title VARCHAR(256);
UPDATE assessment_packages ap SET title=(SELECT a.title FROM assessments a WHERE ap.apid=a.selected_apid);
ALTER TABLE assessment_packages ALTER title SET NOT NULL;
ALTER TABLE assessments DROP name;
ALTER TABLE assessments DROP title;

COMMIT WORK;
