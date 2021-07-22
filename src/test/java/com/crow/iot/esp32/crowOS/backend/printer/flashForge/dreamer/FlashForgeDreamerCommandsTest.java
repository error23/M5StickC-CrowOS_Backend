package com.crow.iot.esp32.crowOS.backend.printer.flashForge.dreamer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author : error23
 * Created : 22/07/2021
 */
class FlashForgeDreamerCommandsTest {

	@Test
	void whenGetExpectedAnswer_thanSuccess() {

		Assertions.assertEquals("CMD M601 Received.", FlashForgeDreamerCommands.HELLO.getExpectedAnswer());
		Assertions.assertEquals("CMD M115 Received.", FlashForgeDreamerCommands.GET_GENERAL_INFO.getExpectedAnswer());
		Assertions.assertEquals("CMD M105 Received.", FlashForgeDreamerCommands.GET_TEMPERATURE.getExpectedAnswer());
		Assertions.assertEquals("CMD M114 Received.", FlashForgeDreamerCommands.GET_POSITIONS.getExpectedAnswer());
		Assertions.assertEquals("CMD M27 Received.", FlashForgeDreamerCommands.GET_PRINTING_PROGRESS.getExpectedAnswer());
		Assertions.assertEquals("CMD M146 Received.", FlashForgeDreamerCommands.SET_COLOR.getExpectedAnswer());
		Assertions.assertEquals("CMD M602 Received.", FlashForgeDreamerCommands.BUY.getExpectedAnswer());

	}
}
