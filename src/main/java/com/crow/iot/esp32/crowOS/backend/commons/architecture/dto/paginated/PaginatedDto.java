package com.crow.iot.esp32.crowOS.backend.commons.architecture.dto.paginated;

import com.crow.iot.esp32.crowOS.backend.commons.architecture.dto.OrderDto;
import lombok.Getter;
import lombok.Setter;

/**
 * @author : error23
 * Created : 30/03/2020
 */
@Getter
@Setter
public class PaginatedDto extends OrderDto {

	/** page number */
	private Integer page;

	/** Number of elements returned per page */
	private Integer numberPerPage;

}
