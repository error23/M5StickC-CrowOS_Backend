package com.crow.iot.esp32.crowOS.backend.security.role;

import com.crow.iot.esp32.crowOS.backend.commons.architecture.dto.IdDto;
import com.crow.iot.esp32.crowOS.backend.security.role.permission.Permission;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author : error23
 * Created : 05/06/2020
 */
@Getter
@Setter
public class RoleDto extends IdDto {

	private Integer priority;
	private String name;
	private Boolean root;
	private List<Permission> permissions;

}
