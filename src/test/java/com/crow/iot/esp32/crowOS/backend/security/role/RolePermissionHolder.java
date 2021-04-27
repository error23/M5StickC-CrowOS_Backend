package com.crow.iot.esp32.crowOS.backend.security.role;

import com.crow.iot.esp32.crowOS.backend.AbstractPermissionHolder;
import com.crow.iot.esp32.crowOS.backend.security.role.permission.Permission;
import com.crow.iot.esp32.crowOS.backend.security.role.permission.Privilege;
import com.crow.iot.esp32.crowOS.backend.security.role.permission.SecuredResource;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : error23
 * Created : 13/06/2020
 */
@Getter
public class RolePermissionHolder extends AbstractPermissionHolder {

	private Permission createRole;

	private Permission readRole;

	private Permission updateRole;

	private Permission deleteRole;

	public RolePermissionHolder() {

		this.createRole = new Permission();
		this.createRole.setSecuredResource(SecuredResource.ROLE);
		this.createRole.setPrivileges(new ArrayList<>(List.of(Privilege.CREATE)));

		this.readRole = new Permission();
		this.readRole.setSecuredResource(SecuredResource.ROLE);
		this.readRole.setPrivileges(new ArrayList<>(List.of(Privilege.READ)));

		this.updateRole = new Permission();
		this.updateRole.setSecuredResource(SecuredResource.ROLE);
		this.updateRole.setPrivileges(new ArrayList<>(List.of(Privilege.UPDATE)));

		this.deleteRole = new Permission();
		this.deleteRole.setSecuredResource(SecuredResource.ROLE);
		this.deleteRole.setPrivileges(new ArrayList<>(List.of(Privilege.DELETE)));

	}

}
