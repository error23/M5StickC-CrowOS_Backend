package com.crow.iot.esp32.crowOS.backend.printer;

import lombok.Getter;
import lombok.Setter;

/**
 * @author : error23
 * Created : 28/05/2021
 */
@Getter
@Setter
public class PrinterDto extends PrinterMinimalDto {

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
