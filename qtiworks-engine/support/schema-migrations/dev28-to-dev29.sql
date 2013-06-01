BEGIN WORK;

-- Remove the 'allow_source' and 'allow_result' columns from 'delivery_settings'.
-- The functinality for this is now controlled by the 'author_mode' column.
ALTER TABLE delivery_settings DROP allow_result;
ALTER TABLE delivery_settings DROP allow_source;

-- Turn author_mode back on for all public delivery settings, as well as the defaults
-- created for each user (unless they've changed it)
UPDATE delivery_settings SET author_mode = true
  WHERE public IS TRUE
    OR title='Default item delivery settings'
    OR title='Default test delivery settings';

-- Change to Assessment -> AssessmentPackage relationships.
-- First change is setting selected package to be the newest one for each row in assessments
ALTER TABLE assessments ADD selected_apid bigint REFERENCES assessment_packages(apid);
UPDATE assessments a SET selected_apid = (SELECT MAX(apid) FROM assessment_packages WHERE aid=a.aid);
ALTER TABLE assessments ALTER selected_apid SET NOT NULL;
ALTER TABLE assessment_packages ALTER aid DROP NOT NULL;

-- New columns in assessment_package. We'll set these to defaults for now;
-- an Engine Manager action should be run to fill in the data appropriately afterwards
ALTER TABLE assessment_packages ADD launchable boolean;
ALTER TABLE assessment_packages ADD error_count int;
ALTER TABLE assessment_packages ADD warning_count int;
UPDATE assessment_packages SET launchable=valid, error_count=0, warning_count=0;
ALTER TABLE assessment_packages ALTER launchable SET NOT NULL;
ALTER TABLE assessment_packages ALTER error_count SET NOT NULL;
ALTER TABLE assessment_packages ALTER warning_count SET NOT NULL;

-- Addition of 'exploded' flag to CandidateSessions
ALTER TABLE candidate_sessions ADD exploded boolean;
UPDATE candidate_sessions SET exploded=false;
ALTER TABLE candidate_sessions ALTER exploded SET NOT NULL;

COMMIT WORK;
