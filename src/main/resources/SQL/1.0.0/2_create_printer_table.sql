-- rgb color
DROP TYPE IF EXISTS COLOR_RGB CASCADE;
CREATE TYPE COLOR_RGB AS ENUM ('RED', 'GREEN', 'BLUE');

-- flash forge dreamer
DROP SEQUENCE IF EXISTS printer_seq CASCADE;
CREATE SEQUENCE printer_seq
	START WITH 1
	INCREMENT BY 50
	NO MINVALUE
	NO MAXVALUE
	CACHE 1;

DROP TABLE IF EXISTS printer CASCADE;
CREATE TABLE printer (
	id BIGINT DEFAULT nextval('printer_seq'),
	created TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
	updated TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
	owner BIGINT NOT NULL,
	updated_by BIGINT,
	version INTEGER NOT NULL DEFAULT 0,
	machine_type VARCHAR,
	machine_name VARCHAR,
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

ALTER TABLE printer ADD CONSTRAINT pk_printer PRIMARY KEY (id);

-- Initial data for printer

INSERT INTO printer (owner, updated_by, version, machine_ip, machine_port)
VALUES (2, 2, 0, '${PRINTER_MACHINE_IP}', 8899);
