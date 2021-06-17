package com.crow.iot.esp32.crowOS.backend.printer.flashForge.dreamer;

import com.crow.iot.esp32.crowOS.backend.printer.ColorRGB;
import com.crow.iot.esp32.crowOS.backend.printer.Printer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author : error23
 * Created : 12/06/2021
 */
@SpringBootTest
@ExtendWith (MockitoExtension.class)
class FlashForgeDreamerClientTest {

	@Spy
	FlashForgeDreamerClient client;

	Printer printer;

	@BeforeEach
	void setUp() {

		this.printer = new Printer();
		this.printer.setId(1L);
		this.printer.setMachineIp("192.168.0.3");
		this.printer.setMachinePort(8899);

	}

	@Test
	void whenUpdateGeneralInfo_thanSuccess() {

		String answer = "CMD M115 Received.\n" +
			"Machine Type: Flashforge Dreamer\n" +
			"Machine Name: error23_dreamer\n" +
			"Firmware: V2.15 20200917\n" +
			"SN: 55003b-324d5015-20393156\n" +
			"X: 230  Y: 150  Z: 140\n" +
			"Tool Count: 2\n" +
			"ok";

		Mockito.doReturn(answer)
		       .when(this.client)
		       .sendCommand(ArgumentMatchers.matches("192.168.0.3"),
		                    ArgumentMatchers.eq(8899),
		                    ArgumentMatchers.eq(FlashForgeDreamerCommands.GET_GENERAL_INFO),
		                    ArgumentMatchers.eq(null));

		this.client.updateGeneralInfo(this.printer);

		assertThat("Flashforge Dreamer").isEqualTo(this.printer.getMachineType());
		assertThat("error23_dreamer").isEqualTo(this.printer.getMachineName());
		assertThat("V2.15 20200917").isEqualTo(this.printer.getFirmware());
		assertThat(230.0).isEqualTo(this.printer.getMaxX());
		assertThat(150.0).isEqualTo(this.printer.getMaxY());
		assertThat(140.0).isEqualTo(this.printer.getMaxZ());
		assertThat(2).isEqualTo(this.printer.getExtruderNumber());

	}

	@Test
	void updateTemperature_thanSuccess() {

		String answer = "CMD M105 Received.\n" +
			"T0:35 /0 T1:20 /0 B:25 /0\n" +
			"ok";

		Mockito.doReturn(answer)
		       .when(this.client)
		       .sendCommand(ArgumentMatchers.matches("192.168.0.3"),
		                    ArgumentMatchers.eq(8899),
		                    ArgumentMatchers.eq(FlashForgeDreamerCommands.GET_TEMPERATURE),
		                    ArgumentMatchers.eq(null));

		this.client.updateTemperature(this.printer);

		assertThat(35).isEqualTo(this.printer.getTemperatureExtruderLeft());
		assertThat(20).isEqualTo(this.printer.getTemperatureExtruderRight());
		assertThat(25).isEqualTo(this.printer.getTemperatureBed());

	}

	@Test
	void updatePositions_thanSuccess() {

		String answer = "CMD M114 Received.\n" +
			"X:123.001 Y:79.9912 Z:10.5 A:0 B:0\n" +
			"ok";

		Mockito.doReturn(answer)
		       .when(this.client)
		       .sendCommand(ArgumentMatchers.matches("192.168.0.3"),
		                    ArgumentMatchers.eq(8899),
		                    ArgumentMatchers.eq(FlashForgeDreamerCommands.GET_POSITIONS),
		                    ArgumentMatchers.eq(null));

		this.client.updatePositions(this.printer);

		assertThat(123.001).isEqualTo(this.printer.getX());
		assertThat(79.9912).isEqualTo(this.printer.getY());
		assertThat(10.5).isEqualTo(this.printer.getZ());

	}

	@Test
	void setColor_thanSuccess() {

		Mockito.doReturn("")
		       .when(this.client)
		       .sendCommand(ArgumentMatchers.matches("192.168.0.3"),
		                    ArgumentMatchers.eq(8899),
		                    ArgumentMatchers.eq(FlashForgeDreamerCommands.SET_COLOR),
		                    ArgumentMatchers.eq("r255 g0 b0 F0"));

		this.client.setColor(this.printer, ColorRGB.RED);
		Mockito.verify(this.client, Mockito.times(1)).sendCommand(ArgumentMatchers.matches("192.168.0.3"),
		                                                          ArgumentMatchers.eq(8899),
		                                                          ArgumentMatchers.eq(FlashForgeDreamerCommands.SET_COLOR),
		                                                          ArgumentMatchers.eq("r255 g0 b0 F0"));

	}

	@Test
	void sendHello_thanSuccess() {

		Mockito.doReturn("")
		       .when(this.client)
		       .sendCommand(ArgumentMatchers.matches("192.168.0.3"),
		                    ArgumentMatchers.eq(8899),
		                    ArgumentMatchers.eq(FlashForgeDreamerCommands.HELLO),
		                    ArgumentMatchers.eq(null));

		this.client.sendHello(this.printer);
		Mockito.verify(this.client, Mockito.times(1)).sendCommand(ArgumentMatchers.matches("192.168.0.3"),
		                                                          ArgumentMatchers.eq(8899),
		                                                          ArgumentMatchers.eq(FlashForgeDreamerCommands.HELLO),
		                                                          ArgumentMatchers.eq(null));

	}

	@Test
	void sendBuy_thanSuccess() {

		Mockito.doReturn("")
		       .when(this.client)
		       .sendCommand(ArgumentMatchers.matches("192.168.0.3"),
		                    ArgumentMatchers.eq(8899),
		                    ArgumentMatchers.eq(FlashForgeDreamerCommands.BUY),
		                    ArgumentMatchers.eq(null));

		this.client.sendBuy(this.printer);
		Mockito.verify(this.client, Mockito.times(1)).sendCommand(ArgumentMatchers.matches("192.168.0.3"),
		                                                          ArgumentMatchers.eq(8899),
		                                                          ArgumentMatchers.eq(FlashForgeDreamerCommands.BUY),
		                                                          ArgumentMatchers.eq(null));

	}

}