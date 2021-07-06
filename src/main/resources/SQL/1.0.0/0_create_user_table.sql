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

-- update sequences set increment by 1
ALTER SEQUENCE account_seq INCREMENT BY 1;
ALTER SEQUENCE role_seq INCREMENT BY 1;

-- Initial data for Account
INSERT INTO account (owner, updated_by, version, first_name, last_name, email, password, enabled, locale)
VALUES (1, NULL, 0, '${ROOT_USER_NAME}', '${ROOT_USER_LAST_NAME}', '${ROOT_USER_EMAIL}', '${ROOT_USER_PASSWORD}', TRUE, LEFT('${LANG}', STRPOS('${LANG}', '.') - 1));

INSERT INTO account (owner, updated_by, version, first_name, last_name, email, password, enabled, locale)
VALUES (1, NULL, 0, '${FLASH_FORGE_USER_NAME}', '${FLASH_FORGE_USER_LAST_NAME}', '${FLASH_FORGE_USER_EMAIL}', '${FLASH_FORGE_USER_PASSWORD}', TRUE, LEFT('${LANG}', STRPOS('${LANG}', '.') - 1));

INSERT INTO account (owner, updated_by, version, first_name, last_name, email, password, enabled, locale)
VALUES (2, NULL, 0, '${ESP_DEV_USER_NAME}', '${ESP_DEV_USER_LAST_NAME}', '${ESP_DEV_USER_EMAIL}', '${ESP_DEV_USER_PASSWORD}', TRUE, LEFT('${LANG}', STRPOS('${LANG}', '.') - 1));

INSERT INTO account (owner, updated_by, version, first_name, last_name, email, password, enabled, locale)
VALUES (2, NULL, 0, '${ESP_USER_NAME}', '${ESP_USER_LAST_NAME}', '${ESP_USER_EMAIL}', '${ESP_USER_PASSWORD}', TRUE, LEFT('${LANG}', STRPOS('${LANG}', '.') - 1));

-- Initial data for Role
INSERT INTO role (owner, updated_by, version, priority, name, root, permissions)
VALUES (1, 1, 0, 1, 'root', TRUE, NULL);

INSERT INTO role (owner, updated_by, version, priority, name, root, permissions)
VALUES (1, 1, 1, 2, 'PRINTER_ROOT', FALSE, '[
  {
    "securedResource": "PRINTER",
    "privileges": [
      "CREATE",
      "READ",
      "UPDATE"
    ]
  },
  {
    "securedResource": "PRINTER_COLOR",
    "privileges": [
      "UPDATE"
    ]
  },
  {
    "securedResource": "PRINTER_MACHINE_ADRESSE",
    "privileges": [
      "UPDATE"
    ]
  }
]');

INSERT INTO role (owner, updated_by, version, priority, name, root, permissions)
VALUES (1, 1, 0, 3, 'DEV', FALSE, '[
  {
    "privileges": [
      "READ"
    ],
    "securedResource": "STACK_TRACE"
  }
]');

INSERT INTO role (owner, updated_by, version, priority, name, root, permissions)
VALUES (1, 1, 0, 4, 'ESP32', FALSE, '[
  {
    "privileges": [
      "READ_OWN",
      "UPDATE_OWN"
    ],
    "securedResource": "ACCOUNT_PASSWORD"
  },
  {
    "privileges": [
      "READ_OWN",
      "UPDATE_OWN"
    ],
    "securedResource": "ACCOUNT"
  },
  {
    "privileges": [
      "CREATE",
      "READ_OWN",
      "UPDATE_OWN"
    ],
    "securedResource": "FEATURE_DATA"
  },
  {
    "securedResource": "PRINTER",
    "privileges": [
      "READ"
    ]
  },
  {
    "securedResource": "PRINTER_COLOR",
    "privileges": [
      "UPDATE"
    ]
  }
]');

-- Initial data for Role Account linking

-- root for first user
INSERT INTO role_l_account (role_id, account_id)
VALUES (1, 1);

-- printer root for second user
INSERT INTO role_l_account (role_id, account_id)
VALUES (2, 2);
INSERT INTO role_l_account (role_id, account_id)
VALUES (3, 2);

-- dev for third user
INSERT INTO role_l_account (role_id, account_id)
VALUES (3, 3);
INSERT INTO role_l_account (role_id, account_id)
VALUES (4, 3);

-- esp32 for other users
INSERT INTO role_l_account (role_id, account_id)
VALUES (4, 4);

-- update sequences set increment by 50
ALTER SEQUENCE account_seq INCREMENT BY 50;
ALTER SEQUENCE role_seq INCREMENT BY 50;

SELECT setval('account_seq', (SELECT max(id) FROM account) + 1);
SELECT setval('role_seq', (SELECT max(id) FROM role) + 1);
