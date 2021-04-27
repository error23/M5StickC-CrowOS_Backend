package com.crow.iot.esp32.crowOS.backend.commons.architecture.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author : error23
 * Created : 30/03/2020
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class IdDto extends AbstractDto {

	/** Entity Id */
	private Long id;

}
