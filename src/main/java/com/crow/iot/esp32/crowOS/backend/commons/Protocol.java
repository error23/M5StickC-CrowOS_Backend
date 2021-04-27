package com.crow.iot.esp32.crowOS.backend.commons;

/**
 * @author : error23
 * Created : 02/04/2020
 */
public enum Protocol {

	/** Http protocol */
	HTTP("http"),

	/** Https protocol */
	HTTPS("https");

	/** String name protocol http or https */
	private final String protocol;

	/**
	 * Creates one protocol with its string name
	 *
	 * @param protocol to create
	 */
	Protocol(String protocol) {

		this.protocol = protocol;
	}

	/**
	 * @return protocol string name
	 */
	@Override
	public String toString() {

		return this.protocol;
	}
}
