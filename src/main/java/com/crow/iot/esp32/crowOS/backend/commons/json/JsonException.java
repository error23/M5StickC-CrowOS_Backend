package com.crow.iot.esp32.crowOS.backend.commons.json;

/**
 * @author : error23
 * Created : 03/04/2020
 */
public class JsonException extends RuntimeException {

	private static final long serialVersionUID = 6296948606110464173L;

	/**
	 * Creates new json exception with it cause and message
	 *
	 * @param message to set
	 * @param e       cause
	 */
	public JsonException(String message, Throwable e) {

		super(message, e);
	}

	/**
	 * Creates new json exception with message
	 *
	 * @param message to set
	 */
	public JsonException(String message) {

		super(message);
	}

}
