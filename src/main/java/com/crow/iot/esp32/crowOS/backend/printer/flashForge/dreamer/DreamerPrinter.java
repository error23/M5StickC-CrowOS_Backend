package com.crow.iot.esp32.crowOS.backend.printer.flashForge.dreamer;

import com.crow.iot.esp32.crowOS.backend.commons.architecture.AbstractEntity;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * @author : error23
 * Created : 28/03/2020
 */
@Entity
@DynamicUpdate
@Table (name = "flash_forge_dreamer")
@Getter
@Setter
public class DreamerPrinter extends AbstractEntity {

	private static final long serialVersionUID = 4042036579697512764L;

	@Id
	@Column (name = "id", nullable = false)
	@Access (AccessType.PROPERTY)
	@GeneratedValue (strategy = GenerationType.SEQUENCE, generator = "flash_forge_dreamer_gen")
	@SequenceGenerator (name = "flash_forge_dreamer_gen", sequenceName = "flash_forge_dreamer_seq")
	@JsonIdentityInfo (generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
	private Long id;

	@Column (name = "machine_type")
	private String machineType;

	@Column (name = "machine_name", unique = true)
	private String machineName;

	@Column (name = "machine_ip")
	private String machineIp;

	@Column (name = "machine_port")
	private Integer machinePort;

	@Column (name = "firmware")
	private String firmware;

	@Column (name = "extruder_number")
	private Integer extruderNumber;

	@Column (name = "led_color")
	@Enumerated (EnumType.STRING)
	private ColorRGB ledColor;

	@Column (name = "x_position")
	private Double x;

	@Column (name = "max_x_position")
	private Double maxX;

	@Column (name = "y_position")
	private Double y;

	@Column (name = "max_y_position")
	private Double maxY;

	@Column (name = "z_position")
	private Double z;

	@Column (name = "max_z_position")
	private Double maxZ;

	@Column (name = "temperature_extruder_left")
	private Integer temperatureExtruderLeft;

	@Column (name = "temperature_extruder_right")
	private Integer temperatureExtruderRight;

	@Column (name = "temperature_bed")
	private Integer temperatureBed;

}
