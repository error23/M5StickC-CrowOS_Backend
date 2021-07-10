package com.crow.iot.esp32.crowOS.backend.printer.flashForge.dreamer;

import com.crow.iot.esp32.crowOS.backend.commons.I18nHelper;
import lombok.Getter;

/**
 * @author : error23
 * Created : 12/06/2021
 */
@Getter
public class FlashForgeDreamerClientException extends RuntimeException {

	private static final long serialVersionUID = 639819186604551130L;

	private String localizedMessage;

	/**
	 * Creates new flash forge dreamer client exception with it cause and message
	 *
	 * @param message to set
	 * @param e       cause
	 */
	public FlashForgeDreamerClientException(String message, Throwable e) {

		super(message, e);
		this.localizedMessage = I18nHelper.getI18n().tr(message);
	}

	/**
	 * Creates new flash forge dreamer client exception with message
	 *
	 * @param message to set
	 */
	public FlashForgeDreamerClientException(String message) {

		super(message);
		this.localizedMessage = I18nHelper.getI18n().tr(message);

	}

}
