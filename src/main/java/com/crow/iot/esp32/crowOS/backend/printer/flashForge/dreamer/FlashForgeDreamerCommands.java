package com.crow.iot.esp32.crowOS.backend.printer.flashForge.dreamer;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author : error23
 * Created : 06/06/2021
 */
@Getter
@AllArgsConstructor
public enum FlashForgeDreamerCommands {

	HELLO("~M601 S1"),
	GET_GENERAL_INFO("~M115"),
	GET_TEMPERATURE("~M105"),
	GET_POSITIONS("~M114"),
	GET_PRINTING_PROGRESS("~M27"),
	SET_COLOR("~M146"),
	BUY("~M602");

	private String commandValue;

	/**
	 * Gets {@link FlashForgeDreamerCommands} expected answer value
	 *
	 * @return expected answer for this command
	 */
	public String getExpectedAnswer() {

		int end = this.commandValue.length();
		if (this.commandValue.contains(" ")) end = this.commandValue.indexOf(" ");

		return "CMD " + this.commandValue.substring(1, end) + " Received.";
	}

}
