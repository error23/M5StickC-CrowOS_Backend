package com.crow.iot.esp32.crowOS.backend.commons.architecture.dto.search;

import com.crow.iot.esp32.crowOS.backend.commons.architecture.dto.AbstractDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * @author : error23
 * Created : 28/03/2020
 */
@Getter
@Setter
@AllArgsConstructor
public class SearchFilter extends AbstractDto {

	/** Database property name */
	@NotNull
	private String property;

	/** Operator that is used to compare property and values */
	@NotNull
	private Operator operator;

	/** List of to be compared with property */
	private List<Object> values;

	/**
	 * Creates new filter
	 *
	 * @param property database property name
	 * @param operator operator that is used to compare property and values
	 * @param values   values to be compared with property
	 */
	public SearchFilter(String property, Operator operator, Object... values) {

		this.property = property;
		this.operator = operator;
		this.values = new ArrayList<>(List.of(values));

	}

}
