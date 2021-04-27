package com.crow.iot.esp32.crowOS.backend.account;

import com.crow.iot.esp32.crowOS.backend.commons.ResourceNotFoundException;
import com.crow.iot.esp32.crowOS.backend.commons.architecture.dto.IdDto;
import com.crow.iot.esp32.crowOS.backend.security.MissingPermissionException;
import com.crow.iot.esp32.crowOS.backend.security.SecurityTools;
import com.crow.iot.esp32.crowOS.backend.security.role.Role;
import com.crow.iot.esp32.crowOS.backend.security.role.RoleService;
import com.crow.iot.esp32.crowOS.backend.security.role.permission.Privilege;
import com.crow.iot.esp32.crowOS.backend.security.role.permission.SecuredResource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;

/**
 * @author : error23
 * Created : 11/06/2020
 */
@SpringBootTest
@ExtendWith (MockitoExtension.class)
class AccountMapperTest {

	AccountDto accountDto;

	Account account;

	Role roleA;

	Role roleB;

	AccountPermissionHolder permissionHolder;

	@MockBean
	RoleService roleService;

	@InjectMocks
	@Autowired
	AccountMapperImpl mapper;

	@BeforeEach
	void setUp() {

		this.permissionHolder = new AccountPermissionHolder();

		this.roleA = new Role();
		this.roleA.setId(1L);
		this.roleA.setName("testA");
		this.roleA.setRoot(false);
		this.roleA.setPriority(1);

		this.roleB = new Role();
		this.roleB.setId(2L);
		this.roleB.setName("testB");
		this.roleB.setRoot(false);
		this.roleB.setPriority(2);

		this.account = new Account();
		this.account.setId(1L);
		this.account.setEnabled(true);
		this.account.setFirstName("igor");
		this.account.setLastName("Rajic");
		this.account.setEmail("error23.d@gmail.com");
		this.account.setPassword("test");
		this.account.setLocale(Locale.FRENCH);
		this.account.setRoles(List.of(this.roleA, this.roleB));

		this.accountDto = new AccountDto();
		this.accountDto.setId(1L);
		this.accountDto.setEnabled(true);
		this.accountDto.setFirstName("igor");
		this.accountDto.setLastName("Rajic");
		this.accountDto.setEmail("error23.d@gmail.com");
		this.accountDto.setPassword("test");
		this.accountDto.setLocale(Locale.FRENCH);
		this.accountDto.setRoles(List.of(new IdDto(this.roleA.getId()), new IdDto(this.roleB.getId())));

		Mockito.when(this.roleService.list(anyList())).thenReturn(List.of(this.roleA, this.roleB));
	}

	@Test
	void whenConvertingToDto_thanSuccess() {

		AccountDto dto = this.mapper.toDto(this.account);
		this.accountDto.setPassword(null);
		this.accountDto.setEnabled(null);
		assertThat(dto.toString()).isEqualTo(this.accountDto.toString());
	}

	@Test
	void whenConvertingToDtoWithPassword_thanSuccess() {

		SecurityTools.setConnectedAccount(this.account);
		this.account.getRoles().get(0).setPermissions(List.of(this.permissionHolder.getReadAccountPassword()));

		AccountDto dto = this.mapper.toDto(this.account);
		this.accountDto.setEnabled(null);
		assertThat(dto.toString()).isEqualTo(this.accountDto.toString());
	}

	@Test
	void whenConvertingToDtoWithPasswordAndEnabled_thanSuccess() {

		this.account.getRoles().get(0).setPermissions((List.of(this.permissionHolder.getReadAccountPassword(), this.permissionHolder.getReadAccountEnabled())));
		SecurityTools.setConnectedAccount(this.account);

		AccountDto dto = this.mapper.toDto(this.account);
		assertThat(dto.toString()).isEqualTo(this.accountDto.toString());
	}

	@Test
	void whenConvertingToEntity_thanFail() {

		MissingPermissionException exception = assertThrows(MissingPermissionException.class, () -> this.mapper.toEntity(this.accountDto));
		assertThat(exception.getPrivilege()).isEqualTo(Privilege.UPDATE);
		assertThat(exception.getSecuredResource()).isEqualTo(SecuredResource.ACCOUNT_PASSWORD);

		SecurityTools.setConnectedAccount(this.account);
		exception = assertThrows(MissingPermissionException.class, () -> this.mapper.toEntity(this.accountDto));
		assertThat(exception.getPrivilege()).isEqualTo(Privilege.UPDATE);
		assertThat(exception.getSecuredResource()).isEqualTo(SecuredResource.ACCOUNT_PASSWORD);

		this.account.getRoles().get(0).setPermissions(List.of(this.permissionHolder.getUpdateAccountPassword()));

		exception = assertThrows(MissingPermissionException.class, () -> this.mapper.toEntity(this.accountDto));
		assertThat(exception.getPrivilege()).isEqualTo(Privilege.UPDATE);
		assertThat(exception.getSecuredResource()).isEqualTo(SecuredResource.ACCOUNT_ENABLED);

		this.account.getRoles().get(0).setPermissions(List.of(this.permissionHolder.getUpdateAccountPassword(), this.permissionHolder.getUpdateAccountEnabled()));

		exception = assertThrows(MissingPermissionException.class, () -> this.mapper.toEntity(this.accountDto));
		assertThat(exception.getPrivilege()).isEqualTo(Privilege.UPDATE);
		assertThat(exception.getSecuredResource()).isEqualTo(SecuredResource.ACCOUNT_ROLE);

	}

	@Test
	void whenConvertingToEntity_thanSuccess() {

		this.account.getRoles().get(0).setPermissions(List.of(this.permissionHolder.getUpdateAccountPassword(), this.permissionHolder.getUpdateAccountEnabled(), this.permissionHolder.getUpdateAccountRole()));
		SecurityTools.setConnectedAccount(this.account);

		Account account = this.mapper.toEntity(this.accountDto);
		assertThat(account).isEqualToComparingFieldByField(this.account);

	}

	@Test
	void whenMerging_thanFail() {

		MissingPermissionException exception = assertThrows(MissingPermissionException.class, () -> this.mapper.merge(this.accountDto, this.account));
		assertThat(exception.getPrivilege()).isEqualTo(Privilege.UPDATE);
		assertThat(exception.getSecuredResource()).isEqualTo(SecuredResource.ACCOUNT_PASSWORD);

		SecurityTools.setConnectedAccount(this.account);

		exception = assertThrows(MissingPermissionException.class, () -> this.mapper.merge(this.accountDto, this.account));
		assertThat(exception.getPrivilege()).isEqualTo(Privilege.UPDATE);
		assertThat(exception.getSecuredResource()).isEqualTo(SecuredResource.ACCOUNT_PASSWORD);

		this.account.getRoles().get(0).setPermissions(List.of(this.permissionHolder.getUpdateAccountPassword()));

		exception = assertThrows(MissingPermissionException.class, () -> this.mapper.merge(this.accountDto, this.account));
		assertThat(exception.getPrivilege()).isEqualTo(Privilege.UPDATE);
		assertThat(exception.getSecuredResource()).isEqualTo(SecuredResource.ACCOUNT_ENABLED);

		this.account.getRoles().get(0).setPermissions(List.of(this.permissionHolder.getUpdateAccountPassword(), this.permissionHolder.getUpdateAccountEnabled()));

		exception = assertThrows(MissingPermissionException.class, () -> this.mapper.merge(this.accountDto, this.account));
		assertThat(exception.getPrivilege()).isEqualTo(Privilege.UPDATE);
		assertThat(exception.getSecuredResource()).isEqualTo(SecuredResource.ACCOUNT_ROLE);

	}

	@Test
	void whenMerging_thanSuccess() {

		SecurityTools.setConnectedAccount(this.account);
		this.account.getRoles().get(0).setPermissions(List.of(this.permissionHolder.getUpdateAccountPassword(), this.permissionHolder.getUpdateAccountEnabled(), this.permissionHolder.getUpdateAccountRole()));

		Account account = this.mapper.merge(this.accountDto, new Account());
		assertThat(account).isEqualToComparingFieldByField(this.account);

	}

	@Test
	void whenGettingRoles_thanFail() {

		MissingPermissionException exception = assertThrows(MissingPermissionException.class, () -> this.mapper.getRoles(this.accountDto, this.account));
		assertThat(exception.getPrivilege()).isEqualTo(Privilege.UPDATE);
		assertThat(exception.getSecuredResource()).isEqualTo(SecuredResource.ACCOUNT_ROLE);

		SecurityTools.setConnectedAccount(this.account);

		exception = assertThrows(MissingPermissionException.class, () -> this.mapper.getRoles(this.accountDto, this.account));
		assertThat(exception.getPrivilege()).isEqualTo(Privilege.UPDATE);
		assertThat(exception.getSecuredResource()).isEqualTo(SecuredResource.ACCOUNT_ROLE);

		this.account.getRoles().get(0).setPermissions(List.of(this.permissionHolder.getUpdateAccountRole()));

		this.accountDto.setRoles(List.of(
			new IdDto(1L),
			new IdDto(2L),
			new IdDto(3L),
			new IdDto(4L),
			new IdDto(5L)));

		ResourceNotFoundException resourceNotFoundException = assertThrows(ResourceNotFoundException.class, () -> this.mapper.getRoles(this.accountDto, this.account));

		assertThat(resourceNotFoundException.getResource()).isEqualTo("Role");
		assertThat(resourceNotFoundException.getIds()).isEqualTo(new Long[] { 3L, 4L, 5L });

	}

	@Test
	void whenGettingRoles_thanSuccess() {

		SecurityTools.setConnectedAccount(this.account);
		this.account.getRoles().get(0).setPermissions(List.of(this.permissionHolder.getUpdateAccountRole()));

		List<Role> roles = this.mapper.getRoles(this.accountDto, this.account);

		assertThat(roles).isNotEmpty();
		assertThat(roles.get(0)).isEqualToComparingFieldByField(this.roleA);
		assertThat(roles.get(1)).isEqualToComparingFieldByField(this.roleB);

	}

}
