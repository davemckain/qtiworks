-- Schema migration script for upgrading from 1.0-beta3 to 1.0-beta4
--
-- NB: This has been written to work with PostgreSQL and will probably need
-- tweaked slightly to work with other databases.
BEGIN WORK;

CREATE SEQUENCE lti_nonce_sequence START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
CREATE TABLE lti_nonces (
    lnid BIGINT PRIMARY KEY NOT NULL,
    message_timestamp TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    consumer_key VARCHAR(256) NOT NULL,
    nonce VARCHAR(256) NOT NULL,
    UNIQUE(consumer_key, nonce)
);

COMMIT WORK;
