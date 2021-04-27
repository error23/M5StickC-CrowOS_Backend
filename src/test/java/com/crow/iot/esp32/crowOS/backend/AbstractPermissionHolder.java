package com.crow.iot.esp32.crowOS.backend;

import com.crow.iot.esp32.crowOS.backend.security.role.permission.Permission;
import com.crow.iot.esp32.crowOS.backend.security.role.permission.Privilege;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : error23
 * Created : 13/06/2020
 */
public class AbstractPermissionHolder {

	/**
	 * Creates new permission with all privileges convert to own
	 *
	 * @param permission to create from
	 * @return new permission
	 */
	public Permission toOwn(Permission permission) {

		List<Privilege> privileges = new ArrayList<>();
		for (Privilege privilege : permission.getPrivileges()) {
			privileges.add(Privilege.toOwn(privilege));
		}

		Permission p = new Permission();
		p.setSecuredResource(permission.getSecuredResource());
		p.setPrivileges(privileges);
		return p;
	}

}
