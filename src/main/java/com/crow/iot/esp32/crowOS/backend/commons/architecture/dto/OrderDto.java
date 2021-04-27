package com.crow.iot.esp32.crowOS.backend.commons.architecture.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * @author : error23
 * Created : 30/03/2020
 */
@Getter
@Setter
public class OrderDto extends AbstractDto {

	private Map<String, OrderDirection> order;

	public enum OrderDirection {
		ASC,
		DESC
	}

}
