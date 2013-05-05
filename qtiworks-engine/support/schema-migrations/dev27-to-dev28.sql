BEGIN WORK;

-- Fix base_type column in candidate_session_outcomes
ALTER TABLE candidate_session_outcomes ALTER base_type DROP NOT NULL;

COMMIT WORK;
