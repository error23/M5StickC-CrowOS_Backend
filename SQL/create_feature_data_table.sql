-- feature_data
DROP SEQUENCE IF EXISTS feature_data_seq CASCADE;
CREATE SEQUENCE feature_data_seq
	START WITH 1
	INCREMENT BY 50
	NO MINVALUE
	NO MAXVALUE
	CACHE 1;

DROP TABLE IF EXISTS feature_data CASCADE;
CREATE TABLE feature_data (
	id BIGINT DEFAULT nextval('feature_data_seq'),
	created TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
	updated TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
	owner BIGINT NOT NULL,
	updated_by BIGINT,
	version INTEGER NOT NULL DEFAULT 0,
	feature_factory_name VARCHAR NOT NULL UNIQUE,
	feature_saved_data JSONB

);

ALTER TABLE feature_data ADD CONSTRAINT pk_feature_data PRIMARY KEY (id);

