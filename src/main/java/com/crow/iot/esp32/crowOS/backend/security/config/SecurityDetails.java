package com.crow.iot.esp32.crowOS.backend.security.config;

import com.crow.iot.esp32.crowOS.backend.account.Account;
import com.crow.iot.esp32.crowOS.backend.security.role.Role;
import com.crow.iot.esp32.crowOS.backend.security.role.permission.Permission;
import com.crow.iot.esp32.crowOS.backend.security.role.permission.Privilege;
import com.crow.iot.esp32.crowOS.backend.security.role.permission.SecuredResource;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author : error23
 * Created : 08/04/2020
 */
public class SecurityDetails implements UserDetails {

	private static final long serialVersionUID = - 6561895727757646568L;

	@Getter
	private Account account;

	/**
	 * Creates new security details from account
	 *
	 * @param account to create from
	 */
	public SecurityDetails(Account account) {

		this.account = account;
		this.generateAuthoritiesFromRole();
	}

	@Override
	public String getUsername() {

		return this.account.getEmail();
	}

	@Override
	public String getPassword() {

		return this.account.getPassword();
	}

	@Override
	public boolean isEnabled() {

		return this.account.isEnabled();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {

		boolean root = false;
		if (this.account.getRoles() != null) {
			for (Role role : this.account.getRoles()) {
				if (role.isRoot()) {
					root = true;
					break;
				}
			}
		}

		List<GrantedAuthority> authorities;
		if (root) {
			authorities = this.generateRootAuthorities();
		}
		else {
			authorities = this.generateAuthoritiesFromRole();
		}

		return authorities;
	}

	/**
	 * Generates authorities for root accounts
	 *
	 * @return list of all possible authorities
	 */
	@NotNull
	private List<GrantedAuthority> generateRootAuthorities() {

		List<GrantedAuthority> authorities = new ArrayList<>();

		for (SecuredResource securedResource : SecuredResource.values()) {
			for (Privilege privilege : Privilege.values()) {
				authorities.add(new SimpleGrantedAuthority(securedResource.name() + "_" + privilege.name()));
			}
		}

		return authorities;
	}

	/**
	 * Gets the authorities from account roles
	 *
	 * @return a list of authorities
	 */
	@NotNull
	private List<GrantedAuthority> generateAuthoritiesFromRole() {

		List<GrantedAuthority> authorities = new ArrayList<>();

		if (this.account.getRoles() == null) return authorities;
		for (Role role : this.account.getRoles()) {
			if (role.getPermissions() == null) continue;
			for (Permission permission : role.getPermissions()) {
				if (permission.getPrivileges() == null) continue;
				for (Privilege privilege : permission.getPrivileges()) {
					SimpleGrantedAuthority authority = new SimpleGrantedAuthority(permission.getSecuredResource().name() + "_" + privilege.name());
					if (! authorities.contains(authority)) {
						authorities.add(authority);
					}
				}
			}
		}
		return authorities;
	}

	@Override
	public boolean isAccountNonExpired() {

		return true;
	}

	@Override
	public boolean isAccountNonLocked() {

		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {

		return true;
	}

}
