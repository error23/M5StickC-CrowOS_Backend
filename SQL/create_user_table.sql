-- role
DROP SEQUENCE IF EXISTS role_seq CASCADE;
CREATE SEQUENCE role_seq
	START WITH 1
	INCREMENT BY 50
	NO MINVALUE
	NO MAXVALUE
	CACHE 1;

DROP TABLE IF EXISTS role CASCADE;
CREATE TABLE role (
	id BIGINT DEFAULT nextval('role_seq'),
	created TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
	updated TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
	owner BIGINT NOT NULL,
	updated_by BIGINT,
	version INTEGER NOT NULL DEFAULT 0,
	priority INTEGER,
	name VARCHAR,
	root BOOLEAN NOT NULL DEFAULT FALSE,
	permissions JSONB
);

ALTER TABLE role ADD CONSTRAINT pk_role PRIMARY KEY (id);

-- account
DROP SEQUENCE IF EXISTS account_seq CASCADE;
CREATE SEQUENCE account_seq
	START WITH 1
	INCREMENT BY 50
	NO MINVALUE
	NO MAXVALUE
	CACHE 1;

DROP TABLE IF EXISTS account CASCADE;
CREATE TABLE account (
	id BIGINT DEFAULT nextval('account_seq'),
	created TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
	updated TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
	owner BIGINT NOT NULL,
	updated_by BIGINT,
	version INTEGER NOT NULL DEFAULT 0,
	first_name VARCHAR,
	last_name VARCHAR,
	email VARCHAR NOT NULL,
	password VARCHAR NOT NULL,
	enabled BOOLEAN NOT NULL DEFAULT TRUE,
	locale VARCHAR
);

ALTER TABLE account ADD CONSTRAINT pk_account PRIMARY KEY (id);
ALTER TABLE account ADD CONSTRAINT uk_account_email UNIQUE (email);

-- role_l_account
DROP TABLE IF EXISTS role_l_account;
CREATE TABLE role_l_account (
	role_id BIGINT NOT NULL,
	account_id BIGINT NOT NULL
);

ALTER TABLE role_l_account ADD CONSTRAINT pk_role_l_account PRIMARY KEY (role_id, account_id);
ALTER TABLE role_l_account ADD CONSTRAINT fk_role_l_account_role_id FOREIGN KEY (role_id) REFERENCES role (id);
ALTER TABLE role_l_account ADD CONSTRAINT fk_role_l_account_account_id FOREIGN KEY (account_id) REFERENCES account (id);

INSERT INTO account(owner, first_name, last_name, email, password, locale)
VALUES (1, 'igor', 'rajic', 'error23.d@gmail.com', '$2y$12$fKL9lE6dTmWIZUUwHbFj.eFxIq9lwt1B1.1tM1KZWhAd5KQUblV0S', 'FR_fr');

INSERT INTO role (owner, priority, name, root)
VALUES (1, 1, 'root', TRUE);

INSERT INTO role_l_account (role_id, account_id)
VALUES (1, 1);
