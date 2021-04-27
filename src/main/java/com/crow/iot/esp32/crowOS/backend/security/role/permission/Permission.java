package com.crow.iot.esp32.crowOS.backend.security.role.permission;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author : error23
 * Created : 13/04/2020
 */
@Getter
@Setter
public class Permission {

	private SecuredResource securedResource;

	private List<Privilege> privileges;

}
