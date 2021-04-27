package com.crow.iot.esp32.crowOS.backend.account;

import com.crow.iot.esp32.crowOS.backend.commons.ResourceNotFoundException;
import com.crow.iot.esp32.crowOS.backend.commons.architecture.dto.AbstractMapper;
import com.crow.iot.esp32.crowOS.backend.commons.architecture.dto.IdDto;
import com.crow.iot.esp32.crowOS.backend.security.SecurityTools;
import com.crow.iot.esp32.crowOS.backend.security.role.Role;
import com.crow.iot.esp32.crowOS.backend.security.role.RoleService;
import com.crow.iot.esp32.crowOS.backend.security.role.permission.Privilege;
import com.crow.iot.esp32.crowOS.backend.security.role.permission.SecuredResource;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.mapstruct.BeforeMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : error23
 * Created : 19/05/2020
 */
@Mapper (config = AbstractMapper.class)
public abstract class AccountMapper implements AbstractMapper<AccountDto, Account> {

	@Setter
	private RoleService roleService;

	@Mapping (target = "password", ignore = true)
	@Mapping (target = "enabled", ignore = true)
	@Override
	public abstract AccountDto toDto(Account entity);

	@Mapping (target = "password", ignore = true)
	@Mapping (target = "enabled", ignore = true)
	@Mapping (target = "roles", expression = "java(getRoles(dto, account))")
	@Override
	public abstract Account toEntity(AccountDto dto);

	@Mapping (target = "password", ignore = true)
	@Mapping (target = "enabled", ignore = true)
	@Mapping (target = "roles", expression = "java(getRoles(dto, entity))")
	@Override
	public abstract Account merge(AccountDto dto, @MappingTarget Account entity);

	@BeforeMapping
	protected void permissionCheck(Account account, @MappingTarget AccountDto accountDto) {

		if (SecurityTools.canConnectedAccount(Privilege.READ, SecuredResource.ACCOUNT_PASSWORD, account)) {
			accountDto.setPassword(account.getPassword());
		}
		if (SecurityTools.canConnectedAccount(Privilege.READ, SecuredResource.ACCOUNT_ENABLED, account)) {
			accountDto.setEnabled(account.isEnabled());
		}
	}

	@BeforeMapping
	protected void permissionCheck(@NotNull AccountDto accountDto, @MappingTarget Account account) {

		if (accountDto.isChanged("password")) {
			SecurityTools.assertCanConnectedAccount(Privilege.UPDATE, SecuredResource.ACCOUNT_PASSWORD, account);
			account.setPassword(accountDto.getPassword());
		}

		if (accountDto.isChanged("enabled")) {
			SecurityTools.assertCanConnectedAccount(Privilege.UPDATE, SecuredResource.ACCOUNT_ENABLED, account);
			account.setEnabled(accountDto.getEnabled());
		}

	}

	protected List<Role> getRoles(@NotNull AccountDto dto, @NotNull Account account) {

		if (! dto.isChanged("roles")) {
			return account.getRoles();
		}

		SecurityTools.assertCanConnectedAccount(Privilege.UPDATE, SecuredResource.ACCOUNT_ROLE, account);
		if (CollectionUtils.isEmpty(dto.getRoles())) return null;

		List<Long> roleIds = new ArrayList<>();
		for (IdDto roleDto : dto.getRoles()) {
			roleIds.add(roleDto.getId());
		}

		List<Role> roles = this.roleService.list(roleIds);

		if (roleIds.size() != roles.size()) {
			for (Role role : roles) {
				roleIds.remove(role.getId());
			}

			throw new ResourceNotFoundException("Role", roleIds.toArray(new Long[0]));
		}

		return roles;
	}
}
