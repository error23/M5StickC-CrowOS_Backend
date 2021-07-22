package com.crow.iot.esp32.crowOS.backend.printer;

import com.crow.iot.esp32.crowOS.backend.account.Account;
import com.crow.iot.esp32.crowOS.backend.account.AccountService;
import com.crow.iot.esp32.crowOS.backend.security.SecurityTools;
import com.crow.iot.esp32.crowOS.backend.security.role.Role;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author : error23
 * Created : 09/07/2021
 */
@SpringBootTest
@AutoConfigureMockMvc
class PrinterEndpointTest {

	@MockBean
	AccountService accountService;

	@MockBean
	PrinterService printerService;

	@Autowired
	MockMvc mvc;

	Account connectedAccount;

	PrinterDto dto;

	List<PrinterDto> dtos;

	List<Printer> printers;

	String dtosString;

	@BeforeEach
	void setUp() throws MethodArgumentNotValidException {

		Role role = new Role();
		role.setId(1L);
		role.setName("testA");
		role.setRoot(true);
		role.setPriority(1);

		this.connectedAccount = new Account();
		this.connectedAccount.setId(1L);
		this.connectedAccount.setEnabled(true);
		this.connectedAccount.setFirstName("error23");
		this.connectedAccount.setLastName("rolly");
		this.connectedAccount.setEmail("error23.d@gmail.com");
		this.connectedAccount.setPassword("test");
		this.connectedAccount.setLocale(Locale.FRENCH);
		this.connectedAccount.setRoles(new ArrayList<>(List.of(role)));
		this.connectedAccount.setOwner(this.connectedAccount);
		Mockito.when(this.accountService.get(1L)).thenReturn(this.connectedAccount);
		SecurityTools.setConnectedAccount(this.connectedAccount);

		this.dtos = new ArrayList<>();
		this.printers = new ArrayList<>();

		for (int i = 0; i < 20; i++) {

			PrinterDto dto = new PrinterDto();
			dto.setId((long) i);

			dto.setMachineType("Flash Forge");
			dto.setMachineName("machine name " + i);
			dto.setMachineIp("192.168.0.0");
			dto.setMachinePort(8080);
			this.dtos.add(dto);

			Printer printer = new Printer();
			printer.setId((long) i);
			printer.setOwner(this.connectedAccount);

			printer.setMachineName("machine name " + i);
			printer.setMachineIp("192.168.0.0");
			printer.setMachinePort(8080);
			printer.setExtruderNumber(2);
			printer.setMaxX(100D);
			printer.setMaxY(100D);
			printer.setMaxZ(100D);
			printer.setFirmware("firmware " + i);
			printer.setLedColor(ColorRGB.RED);
			printer.setTemperatureExtruderLeft(200);
			printer.setTemperatureExtruderRight(150);
			printer.setTemperatureBed(50);
			printer.setX(50D);
			printer.setY(60D);
			printer.setZ(30D);
			printer.setMachineType("Flash Forge");

			this.printers.add(printer);
		}

		this.dto = new PrinterDto();
		this.dto.setId(0L);

		this.dto.setMachineType("Flash Forge");
		this.dto.setMachineName("machine name 0");
		this.dto.setMachineIp("192.168.0.0");
		this.dto.setMachinePort(8080);

		this.dto.setExtruderNumber(2);
		this.dto.setMaxX(100D);
		this.dto.setMaxY(100D);
		this.dto.setMaxZ(100D);
		this.dto.setFirmware("firmware 0");
		this.dto.setLedColor(ColorRGB.RED);
		this.dto.setTemperatureExtruderLeft(200);
		this.dto.setTemperatureExtruderRight(150);
		this.dto.setTemperatureBed(50);
		this.dto.setX(50D);
		this.dto.setY(60D);
		this.dto.setZ(30D);

		this.dtosString = "[" + StringUtils.join(this.dtos, ",") + "]";

		Mockito.when(this.printerService.list()).thenReturn(this.printers);
		Mockito.when(this.printerService.get(0L)).thenReturn(this.printers.get(0));

	}

	@Test
	void whenList_thanSuccess() throws Exception {

		this.mvc.perform(get("/printer"))
		        .andDo(log())
		        .andExpect(status().isOk())
		        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
		        .andExpect(content().encoding(StandardCharsets.UTF_8.name()))
		        .andExpect(content().json(this.dtosString, true));

		verify(this.printerService, times(1)).list();

	}

	@Test
	void whenGet_thanSuccess() throws Exception {

		this.mvc.perform(get("/printer/0"))
		        .andDo(log())
		        .andExpect(status().isOk())
		        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
		        .andExpect(content().encoding(StandardCharsets.UTF_8.name()))
		        .andExpect(content().json(this.dto.toString(), true));

		verify(this.printerService, times(1)).get(0L);

	}

	@Test
	void whenUpdateColor_thanSuccess() throws Exception {

		this.mvc.perform(patch("/printer/{printerId}/color?color={color}", 0, "RED"))
		        .andDo(log())
		        .andExpect(status().isAccepted())
		        .andExpect(content().encoding(StandardCharsets.UTF_8.name()));

		verify(this.printerService, times(1)).get(0L);
		verify(this.printerService, times(1)).updateLedColor(ArgumentMatchers.any(Printer.class), ArgumentMatchers.any(ColorRGB.class));
	}

	@Test
	void whenUpdateMachineAdresse_thanSuccess() throws Exception {

		this.mvc.perform(patch("/printer/{printerId}/adresse?machineIp={machineIp}&machinePort={machinePort}", 0, "192.168.0.0", 80))
		        .andDo(log())
		        .andExpect(status().isAccepted())
		        .andExpect(content().encoding(StandardCharsets.UTF_8.name()));

		verify(this.printerService, times(1)).get(0L);
		verify(this.printerService, times(1)).updateMachineAdresse(ArgumentMatchers.any(Printer.class), ArgumentMatchers.matches("192.168.0.0"), ArgumentMatchers.eq(80));
	}
}
