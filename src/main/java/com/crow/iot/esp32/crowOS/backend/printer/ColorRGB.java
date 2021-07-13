package com.crow.iot.esp32.crowOS.backend.printer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * @author : error23
 * Created : 28/05/2021
 */
@Getter
@AllArgsConstructor
public enum ColorRGB {

	WHITE(255, 255, 255),
	RED(255, 0, 0),
	GREEN(0, 255, 0),
	BLUE(0, 0, 255),
	PURPLE(141, 0, 255);

	private int red;
	private int green;
	private int blue;

	@NotNull
	@Contract (pure = true)
	@Override
	public String toString() {

		return "r" + this.red + " g" + this.green + " b" + this.blue + " F0";

	}
}
