package com.crow.iot.esp32.crowOS.backend.commons;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

/**
 * @author : error23
 * Created : 02/05/2021
 */
@Getter
public class DuplicatedResourceException extends RuntimeException {

	private static final long serialVersionUID = 3317385159486334743L;

	private String localizedMessage;

	private String resource;

	private String parameter;

	private String value;

	public DuplicatedResourceException(@NotNull String resource, String parameter, String value) {

		this.localizedMessage = I18nHelper.getI18n().tr("Sorry, one {0} already exists with {1} = {2}", resource, parameter, value);
		this.resource = resource;
		this.parameter = parameter;
		this.value = value;

	}

}
