package com.crow.iot.esp32.crowOS.backend.commons.architecture.dto.paginated;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author : error23
 * Created : 30/03/2020
 */
@Getter
@Setter
public class PaginatedResult<E> {

	/** Returned results */
	private List<E> results;

	/** Total count of results in database */
	private Integer totalCount;

	/**
	 * Creates new {@link PaginatedResult} with results and total count
	 *
	 * @param results    returned by query
	 * @param totalCount total count of results in database
	 */
	public PaginatedResult(List<E> results, Integer totalCount) {

		this.results = results;
		this.totalCount = totalCount;
	}

}
