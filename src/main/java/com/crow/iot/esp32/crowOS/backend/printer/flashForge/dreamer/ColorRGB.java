package com.crow.iot.esp32.crowOS.backend.printer.flashForge.dreamer;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author : error23
 * Created : 28/05/2021
 */
@Getter
@AllArgsConstructor
public enum ColorRGB {

	RED(255, 0, 0),
	GREEN(0, 255, 0),
	BLUE(0, 0, 255);

	private int red;
	private int green;
	private int blue;

}
