package com.crow.iot.esp32.crowOS.backend.printer;

import com.crow.iot.esp32.crowOS.backend.commons.architecture.dto.IdDto;
import lombok.Getter;
import lombok.Setter;

/**
 * @author : error23
 * Created : 29/05/2021
 */
@Getter
@Setter
public class PrinterMinimalDto extends IdDto {

	private String machineType;
	private String machineName;
	private String machineIp;
	private Integer machinePort;
}
