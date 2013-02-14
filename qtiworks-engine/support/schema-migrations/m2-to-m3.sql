-- PostgreSQL DB migration from 1.0-M2 to 1.0-M3
ALTER TABLE candidate_responses ADD xeid BIGINT;
UPDATE candidate_responses xr SET xeid = xa.xeid FROM candidate_attempts xa WHERE xa.xaid = xr.xaid;
ALTER TABLE candidate_responses ALTER xeid SET NOT NULL;
ALTER TABLE candidate_events DROP event_category;
ALTER TABLE candidate_responses DROP xaid;
ALTER TABLE candidate_responses ADD CONSTRAINT fkac8f3c9656ff5039 FOREIGN KEY (xeid) REFERENCES candidate_events(xeid);
DROP TABLE candidate_attempts;
DROP SEQUENCE candidate_attempt_sequence;
