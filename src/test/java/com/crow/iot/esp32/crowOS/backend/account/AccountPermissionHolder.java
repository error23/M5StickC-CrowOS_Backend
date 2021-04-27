package com.crow.iot.esp32.crowOS.backend.account;

import com.crow.iot.esp32.crowOS.backend.AbstractPermissionHolder;
import com.crow.iot.esp32.crowOS.backend.security.role.permission.Permission;
import com.crow.iot.esp32.crowOS.backend.security.role.permission.Privilege;
import com.crow.iot.esp32.crowOS.backend.security.role.permission.SecuredResource;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : error23
 * Created : 12/06/2020
 */
@Getter
public class AccountPermissionHolder extends AbstractPermissionHolder {

	private Permission createAccount;

	private Permission readAccount;

	private Permission updateAccount;

	private Permission deleteAccount;

	private Permission readAccountPassword;

	private Permission updateAccountPassword;

	private Permission readAccountEnabled;

	private Permission updateAccountEnabled;

	private Permission readAccountRole;

	private Permission updateAccountRole;

	public AccountPermissionHolder() {

		this.createAccount = new Permission();
		this.createAccount.setSecuredResource(SecuredResource.ACCOUNT);
		this.createAccount.setPrivileges(new ArrayList<>(List.of(Privilege.CREATE)));

		this.readAccount = new Permission();
		this.readAccount.setSecuredResource(SecuredResource.ACCOUNT);
		this.readAccount.setPrivileges(new ArrayList<>(List.of(Privilege.READ)));

		this.updateAccount = new Permission();
		this.updateAccount.setSecuredResource(SecuredResource.ACCOUNT);
		this.updateAccount.setPrivileges(new ArrayList<>(List.of(Privilege.UPDATE)));

		this.deleteAccount = new Permission();
		this.deleteAccount.setSecuredResource(SecuredResource.ACCOUNT);
		this.deleteAccount.setPrivileges(new ArrayList<>(List.of(Privilege.DELETE)));

		this.readAccountPassword = new Permission();
		this.readAccountPassword.setSecuredResource(SecuredResource.ACCOUNT_PASSWORD);
		this.readAccountPassword.setPrivileges(new ArrayList<>(List.of(Privilege.READ)));

		this.updateAccountPassword = new Permission();
		this.updateAccountPassword.setSecuredResource(SecuredResource.ACCOUNT_PASSWORD);
		this.updateAccountPassword.setPrivileges(new ArrayList<>(List.of(Privilege.UPDATE)));

		this.readAccountEnabled = new Permission();
		this.readAccountEnabled.setSecuredResource(SecuredResource.ACCOUNT_ENABLED);
		this.readAccountEnabled.setPrivileges(new ArrayList<>(List.of(Privilege.READ)));

		this.updateAccountEnabled = new Permission();
		this.updateAccountEnabled.setSecuredResource(SecuredResource.ACCOUNT_ENABLED);
		this.updateAccountEnabled.setPrivileges(new ArrayList<>(List.of(Privilege.UPDATE)));

		this.readAccountRole = new Permission();
		this.readAccountRole.setSecuredResource(SecuredResource.ACCOUNT_ROLE);
		this.readAccountRole.setPrivileges(new ArrayList<>(List.of(Privilege.READ)));

		this.updateAccountRole = new Permission();
		this.updateAccountRole.setSecuredResource(SecuredResource.ACCOUNT_ROLE);
		this.updateAccountRole.setPrivileges(new ArrayList<>(List.of(Privilege.UPDATE)));

	}

}
