package com.crow.iot.esp32.crowOS.backend.commons.architecture.dto.search;

import com.crow.iot.esp32.crowOS.backend.commons.architecture.dto.paginated.PaginatedDto;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.CollectionUtils;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * @author : error23
 * Created : 28/03/2020
 */
@Getter
@Setter
public class SearchDto extends PaginatedDto {

	/** List of {@link SearchFilter} to search with */
	@NotNull
	private ArrayList<SearchFilter> searchFilters;

	/** Custom string search default id search */
	private String search;

	/**
	 * Creates {@link SearchDto} with its searchFilters
	 *
	 * @param searchFilters searchFilters to search wit
	 */
	public SearchDto(SearchFilter... searchFilters) {

		this.searchFilters = new ArrayList<>(List.of(searchFilters));

	}

	/**
	 * Adds one searchFilter
	 *
	 * @param searchFilter to be added
	 */
	public void addFilter(SearchFilter searchFilter) {

		this.searchFilters.add(searchFilter);
	}

	/**
	 * Removes one searchFilter
	 *
	 * @param searchFilter to be removed
	 */
	public void removeFilter(SearchFilter searchFilter) {

		this.searchFilters.remove(searchFilter);
	}

	/**
	 * Gets one filter by its property
	 *
	 * @param property to search for
	 * @return filter containing property
	 */
	public SearchFilter getFilter(String property) {

		if (CollectionUtils.isEmpty(this.searchFilters)) return null;
		for (SearchFilter searchFilter : this.searchFilters) {
			if (searchFilter.getProperty().equals(property)) return searchFilter;
		}
		return null;
	}

}
