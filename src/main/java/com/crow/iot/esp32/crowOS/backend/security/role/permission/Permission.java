package com.crow.iot.esp32.crowOS.backend.security.role.permission;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

/**
 * @author : error23
 * Created : 13/04/2020
 */
@Getter
@Setter
public class Permission {

	private SecuredResource securedResource;

	private List<Privilege> privileges;

	@Override
	public boolean equals(Object o) {

		if (this == o) return true;
		if (! (o instanceof Permission)) return false;
		Permission that = (Permission) o;
		return this.getSecuredResource() == that.getSecuredResource() &&
			this.getPrivileges().equals(that.getPrivileges());
	}

	@Override
	public int hashCode() {

		return Objects.hash(this.getSecuredResource(), this.getPrivileges());
	}

}
