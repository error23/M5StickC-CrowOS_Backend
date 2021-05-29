-- rgb color
DROP TYPE IF EXISTS COLOR_RGB CASCADE;
CREATE TYPE COLOR_RGB AS ENUM ('RED', 'GREEN', 'BLUE');

-- flash forge dreamer
DROP SEQUENCE IF EXISTS flash_forge_dreamer_seq CASCADE;
CREATE SEQUENCE flash_forge_dreamer_seq
	START WITH 1
	INCREMENT BY 50
	NO MINVALUE
	NO MAXVALUE
	CACHE 1;

DROP TABLE IF EXISTS flash_forge_dreamer CASCADE;
CREATE TABLE flash_forge_dreamer (
	id BIGINT DEFAULT nextval('flash_forge_dreamer_seq'),
	created TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
	updated TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
	owner BIGINT NOT NULL,
	updated_by BIGINT,
	version INTEGER NOT NULL DEFAULT 0,
	machine_type VARCHAR,
	machine_name VARCHAR UNIQUE,
	machine_ip VARCHAR UNIQUE,
	machine_port INTEGER,
	firmware VARCHAR,
	extruder_number INTEGER,
	led_color COLOR_RGB,
	x_position DOUBLE PRECISION,
	max_x_position DOUBLE PRECISION,
	y_position DOUBLE PRECISION,
	max_y_position DOUBLE PRECISION,
	z_position DOUBLE PRECISION,
	max_z_position DOUBLE PRECISION,
	temperature_extruder_left INTEGER,
	temperature_extruder_right INTEGER,
	temperature_bed INTEGER
);

ALTER TABLE flash_forge_dreamer ADD CONSTRAINT pk_flash_forge_dreamer PRIMARY KEY (id);

