package com.crow.iot.esp32.crowOS.backend.printer;

import com.crow.iot.esp32.crowOS.backend.commons.architecture.dto.IdDto;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * @author : error23
 * Created : 29/05/2021
 */
@Getter
@Setter
public class PrinterMinimalDto extends IdDto {

	private String machineType;

	@NotNull
	private String machineName;

	@NotNull
	private String machineIp;

	@NotNull
	private Integer machinePort;
}
