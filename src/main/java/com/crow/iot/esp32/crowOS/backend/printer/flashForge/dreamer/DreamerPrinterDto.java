package com.crow.iot.esp32.crowOS.backend.printer.flashForge.dreamer;

import com.crow.iot.esp32.crowOS.backend.commons.architecture.dto.IdDto;
import lombok.Getter;
import lombok.Setter;

/**
 * @author : error23
 * Created : 28/05/2021
 */
@Getter
@Setter
public class DreamerPrinterDto extends IdDto {

	private String machineType;
	private String machineName;
	private String machineIp;
	private Integer machinePort;

	private String firmware;
	private Integer extruderNumber;
	private ColorRGB ledColor;

	private Double x;
	private Double maxX;

	private Double y;
	private Double maxY;

	private Double z;
	private Double maxZ;

	private Integer temperatureExtruderLeft;
	private Integer temperatureExtruderRight;
	private Integer temperatureBed;
}
