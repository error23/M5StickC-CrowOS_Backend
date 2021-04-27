package com.crow.iot.esp32.crowOS.backend.commons.architecture.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * @author : error23
 * Created : 22/05/2020
 */
@Getter
@Setter
@Builder
public class ExceptionDto extends AbstractDto {

	private String error;
	private String detailsHumanReadable;
	private String stackTrace;
	private String locale;

}
