package com.crow.iot.esp32.crowOS.backend.printer.flashForge.dreamer;

import com.crow.iot.esp32.crowOS.backend.printer.ColorRGB;
import com.crow.iot.esp32.crowOS.backend.printer.Printer;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * @author : error23
 * Created : 29/05/2021
 */
@Component
public class FlashForgeDreamerClient {

	@Value ("${flashforge.dreamer.schedule.retriesUntilFail}")
	private int retriesUntilFail;

	/**
	 * Update database {@link Printer} general infos from physical flash forge dreamer printer
	 *
	 * @param printer to update
	 */
	public void updateGeneralInfo(@NotNull Printer printer) {

		String[] result = this.sendCommand(printer.getMachineIp(), printer.getMachinePort(), FlashForgeDreamerCommands.GET_GENERAL_INFO, null).split("\n");

		printer.setMachineType(result[1].split(":")[1].trim());
		printer.setMachineName(result[2].split(":")[1].trim());
		printer.setFirmware(result[3].split(":")[1].trim());

		String[] maxPositions = result[5].split(":");
		printer.setMaxX(Double.parseDouble(maxPositions[1].trim().split(" ")[0]));
		printer.setMaxY(Double.parseDouble(maxPositions[2].trim().split(" ")[0]));
		printer.setMaxZ(Double.parseDouble(maxPositions[3].trim().split(" ")[0]));

		printer.setExtruderNumber(Integer.parseInt(result[6].split(":")[1].trim()));

	}

	/**
	 * Update database {@link Printer} temperatures from physical flash forge dreamer printer
	 *
	 * @param printer to update
	 */
	public void updateTemperature(@NotNull Printer printer) {

		String result = this.sendCommand(printer.getMachineIp(), printer.getMachinePort(), FlashForgeDreamerCommands.GET_TEMPERATURE, null);
		String[] positions = result.split("\n")[1].split(" ");

		printer.setTemperatureExtruderLeft(Integer.parseInt(positions[0].substring(3)));
		printer.setTemperatureExtruderRight(Integer.parseInt(positions[2].substring(3)));
		printer.setTemperatureBed(Integer.parseInt(positions[4].substring(2)));

	}

	/**
	 * Update database {@link Printer} positions from physical flash forge dreamer printer
	 *
	 * @param printer to update
	 */
	public void updatePositions(@NotNull Printer printer) {

		String result = this.sendCommand(printer.getMachineIp(), printer.getMachinePort(), FlashForgeDreamerCommands.GET_POSITIONS, null);
		String[] positions = result.split("\n")[1].split(" ");

		printer.setX(Double.parseDouble(positions[0].substring(2)));
		printer.setY(Double.parseDouble(positions[1].substring(2)));
		printer.setZ(Double.parseDouble(positions[2].substring(2)));

	}

	/**
	 * Set flash forge dreamer led color
	 *
	 * @param printer to set to
	 * @param color   color to set
	 */
	public void setColor(@NotNull Printer printer, @NotNull ColorRGB color) {

		this.sendCommand(printer.getMachineIp(), printer.getMachinePort(), FlashForgeDreamerCommands.SET_COLOR, color.toString());

	}

	/**
	 * Send {@link FlashForgeDreamerCommands#HELLO} command to flash forge dreamer printer
	 *
	 * @param printer to send to
	 */
	public void sendHello(@NotNull Printer printer) {

		this.sendCommand(printer.getMachineIp(), printer.getMachinePort(), FlashForgeDreamerCommands.HELLO, null);
	}

	/**
	 * Send {@link FlashForgeDreamerCommands#BUY} command to flash forge dreamer printer
	 *
	 * @param printer to send to
	 */
	public void sendBuy(@NotNull Printer printer) {

		this.sendCommand(printer.getMachineIp(), printer.getMachinePort(), FlashForgeDreamerCommands.BUY, null);

	}

	/**
	 * Sends one command to flash forge dreamer printer
	 *
	 * @param ipAddress printer ip adresse
	 * @param port      printer port
	 * @param command   command to send
	 * @param message   custom message sent just after command
	 * @return printer answer
	 */
	@NotNull
	protected String sendCommand(@NotNull String ipAddress, @NotNull Integer port, @NotNull FlashForgeDreamerCommands command, String message) {

		StringBuilder builder = new StringBuilder();

		try (
			Socket socket = new Socket(ipAddress, port);
			PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))
		) {

			writer.println(command.getCommandValue() + " " + message);

			int i = 0;
			String buffer = null;
			while (! command.getExpectedAnswer().equals(buffer)) {
				buffer = reader.readLine();
				if (++ i >= this.retriesUntilFail) throw new FlashForgeDreamerClientException("Error retrieving command " + command + " answer.");
			}

			builder.append(buffer).append("\n");
			while (! buffer.equals("ok")) {
				buffer = reader.readLine();
				builder.append(buffer).append("\n");
			}

		}
		catch (IOException e) {
			throw new FlashForgeDreamerClientException("Cannot send command to FlashForge Dreamer : ", e);
		}

		return builder.toString();

	}
}
