package com.crow.iot.esp32.crowOS.backend.security.role;

import com.crow.iot.esp32.crowOS.backend.account.Account;
import com.crow.iot.esp32.crowOS.backend.commons.ResourceNotFoundException;
import com.crow.iot.esp32.crowOS.backend.security.MissingPermissionException;
import com.crow.iot.esp32.crowOS.backend.security.SecurityTools;
import com.crow.iot.esp32.crowOS.backend.security.role.permission.Privilege;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author : error23
 * Created : 13/06/2020
 */
@SpringBootTest
@Slf4j
class RoleServiceTest {

	@MockBean
	RoleDao roleDao;

	@Autowired
	@InjectMocks
	RoleService roleService;

	RoleDto dto;

	Role role;

	Role ownRole;

	Account connectedAccount;

	RolePermissionHolder permissionHolder;

	@BeforeEach
	void setUp() {

		this.permissionHolder = new RolePermissionHolder();

		this.dto = new RoleDto();
		this.dto.setPriority(1);
		this.dto.setName("accountRole");

		this.ownRole = new Role();
		this.ownRole.setId(1L);
		this.ownRole.setPriority(1);
		this.ownRole.setName("accountRole");
		this.ownRole.setRoot(false);
		when(this.roleDao.get(this.ownRole.getId())).thenReturn(this.ownRole);

		this.role = new Role();
		this.role.setId(2L);
		this.role.setPriority(2);
		this.role.setName("accountRole");
		this.role.setRoot(false);
		when(this.roleDao.get(this.role.getId())).thenReturn(this.role);

		this.connectedAccount = new Account();
		this.connectedAccount.setId(2L);
		this.connectedAccount.setEnabled(true);
		this.connectedAccount.setFirstName("error23");
		this.connectedAccount.setLastName("rolly");
		this.connectedAccount.setEmail("error23.d@gmail.com");
		this.connectedAccount.setPassword("test");
		this.connectedAccount.setLocale(Locale.FRENCH);
		this.connectedAccount.setRoles(new ArrayList<>(List.of(this.role)));

		this.ownRole.setOwner(this.connectedAccount);

	}

	@Test
	void whenGet_thanFail() {

		assertThrows(AuthenticationCredentialsNotFoundException.class, () -> this.roleService.get(this.role.getId()));
		SecurityTools.setConnectedAccount(this.connectedAccount);
		assertThrows(MissingPermissionException.class, () -> this.roleService.get(this.role.getId()));
		assertThrows(MissingPermissionException.class, () -> this.roleService.get(this.ownRole.getId()));
		assertThrows(ResourceNotFoundException.class, () -> this.roleService.get(10000L));

		this.connectedAccount.getRoles().get(0).setPermissions(new ArrayList<>(List.of(this.permissionHolder.toOwn(this.permissionHolder.getReadRole()))));
		assertThrows(MissingPermissionException.class, () -> this.roleService.get(this.role.getId()));

		this.connectedAccount.getRoles().get(0).getPermissions().get(0).getPrivileges().add(Privilege.READ);
		assertThrows(MethodArgumentNotValidException.class, () -> this.roleService.get(null));
	}

	@Test
	void whenGet_thanSuccess() throws MethodArgumentNotValidException {

		SecurityTools.setConnectedAccount(this.connectedAccount);
		this.connectedAccount.getRoles().get(0).setPermissions(new ArrayList<>(List.of(this.permissionHolder.toOwn(this.permissionHolder.getReadRole()))));
		assertThat(this.roleService.get(this.ownRole.getId())).isNotNull();

		this.connectedAccount.getRoles().get(0).getPermissions().get(0).getPrivileges().add(Privilege.READ);
		assertThat(this.roleService.get(this.role.getId())).isNotNull();

	}

	@Test
	void whenList_thanFail() {

		when(this.roleDao.search(any())).thenReturn(List.of(this.ownRole));
		assertThrows(AuthenticationCredentialsNotFoundException.class, () -> this.roleService.list(List.of(this.ownRole.getId())));

		SecurityTools.setConnectedAccount(this.connectedAccount);
		assertThrows(MissingPermissionException.class, () -> this.roleService.list(List.of(this.ownRole.getId())));

		this.connectedAccount.getRoles().get(0).setPermissions(new ArrayList<>(List.of(this.permissionHolder.toOwn(this.permissionHolder.getReadRole()))));
		when(this.roleDao.search(any())).thenReturn(List.of(this.ownRole, this.role));
		assertThrows(MissingPermissionException.class, () -> this.roleService.list(List.of(this.ownRole.getId(), this.role.getId())));

	}

	@Test
	void whenList_thanSuccess() {

		when(this.roleDao.search(any())).thenReturn(List.of(this.ownRole));

		SecurityTools.setConnectedAccount(this.connectedAccount);
		this.connectedAccount.getRoles().get(0).setPermissions(new ArrayList<>(List.of(this.permissionHolder.toOwn(this.permissionHolder.getReadRole()))));

		assertThat(this.roleService.list(List.of(this.ownRole.getId())))
			.isNotEmpty()
			.hasSize(1);

		when(this.roleDao.search(any())).thenReturn(List.of(this.ownRole, this.role));
		this.connectedAccount.getRoles().get(0).getPermissions().get(0).getPrivileges().add(Privilege.READ);

		assertThat(this.roleService.list(List.of(this.ownRole.getId(), this.role.getId())))
			.isNotEmpty()
			.hasSize(2);

	}

	@Test
	void whenCreate_thanFail() {

		assertThrows(AuthenticationCredentialsNotFoundException.class, () -> this.roleService.create(this.dto));

		SecurityTools.setConnectedAccount(this.connectedAccount);
		assertThrows(MissingPermissionException.class, () -> this.roleService.create(this.dto));

		this.connectedAccount.getRoles().get(0).setPermissions(new ArrayList<>(List.of(this.permissionHolder.getCreateRole())));
		this.dto.setRoot(true);
		assertThrows(MissingPermissionException.class, () -> this.roleService.create(this.dto));

	}

	@Test
	void whenCreate_thenSuccess() {

		SecurityTools.setConnectedAccount(this.connectedAccount);

		this.connectedAccount.getRoles().get(0).setPermissions(new ArrayList<>(List.of(this.permissionHolder.getCreateRole())));
		assertThat(this.roleService.create(this.dto)).isNotNull();

		this.connectedAccount.getRoles().get(0).setPermissions(new ArrayList<>(List.of(this.permissionHolder.getCreateRole())));
		this.connectedAccount.getRoles().get(0).setRoot(true);
		this.dto.setRoot(true);
		assertThat(this.roleService.create(this.dto)).isNotNull();

	}

	@Test
	void whenUpdate_thenFail() {

		this.dto.setName("test");
		this.dto.setPriority(3);

		assertThrows(AuthenticationCredentialsNotFoundException.class, () -> this.roleService.update(this.ownRole, this.dto));

		SecurityTools.setConnectedAccount(this.connectedAccount);
		assertThrows(MissingPermissionException.class, () -> this.roleService.update(this.ownRole, this.dto));

		this.connectedAccount.getRoles().get(0).setPermissions(new ArrayList<>(List.of(this.permissionHolder.toOwn(this.permissionHolder.getUpdateRole()))));
		assertThrows(MissingPermissionException.class, () -> this.roleService.update(this.role, this.dto));

		this.dto.setRoot(true);
		this.connectedAccount.getRoles().get(0).setPermissions(new ArrayList<>(List.of(this.permissionHolder.toOwn(this.permissionHolder.getUpdateRole()))));
		assertThrows(MissingPermissionException.class, () -> this.roleService.update(this.ownRole, this.dto));

		this.connectedAccount.getRoles().get(0).getPermissions().get(0).getPrivileges().add(Privilege.UPDATE);
		assertThrows(MissingPermissionException.class, () -> this.roleService.update(this.role, this.dto));

	}

	@Test
	void whenUpdate_thanSuccess() {

		this.dto.setName("test");
		this.dto.setPriority(3);

		SecurityTools.setConnectedAccount(this.connectedAccount);
		this.connectedAccount.getRoles().get(0).setPermissions(new ArrayList<>(List.of(this.permissionHolder.toOwn(this.permissionHolder.getUpdateRole()))));
		this.roleService.update(this.ownRole, this.dto);

		assertThat(this.ownRole.getName()).isEqualTo(this.dto.getName());
		assertThat(this.ownRole.getPriority()).isEqualTo(this.dto.getPriority());

		this.connectedAccount.getRoles().get(0).getPermissions().get(0).getPrivileges().add(Privilege.UPDATE);
		this.roleService.update(this.role, this.dto);

		assertThat(this.role.getName()).isEqualTo(this.dto.getName());
		assertThat(this.role.getPriority()).isEqualTo(this.dto.getPriority());

		this.role.setRoot(true);
		this.dto.setRoot(false);
		this.roleService.update(this.role, this.dto);

		assertThat(this.role.isRoot()).isEqualTo(this.dto.getRoot());

	}

	@Test
	void whenDeleting_thenFail() {

		assertThrows(AuthenticationCredentialsNotFoundException.class, () -> this.roleService.delete(this.role));

		SecurityTools.setConnectedAccount(this.connectedAccount);
		assertThrows(MissingPermissionException.class, () -> this.roleService.delete(this.role));

		this.connectedAccount.getRoles().get(0).setPermissions(new ArrayList<>(List.of(this.permissionHolder.toOwn(this.permissionHolder.getDeleteRole()))));
		assertThrows(MissingPermissionException.class, () -> this.roleService.delete(this.role));

	}

	@Test
	void whenDeleting_thenSuccess() {

		SecurityTools.setConnectedAccount(this.connectedAccount);
		this.connectedAccount.getRoles().get(0).setPermissions(new ArrayList<>(List.of(this.permissionHolder.toOwn(this.permissionHolder.getDeleteRole()))));
		this.roleService.delete(this.ownRole);
		verify(this.roleDao, times(1)).delete(this.ownRole);

		this.connectedAccount.getRoles().get(0).getPermissions().get(0).getPrivileges().add(Privilege.DELETE);
		this.roleService.delete(this.role);
		verify(this.roleDao, times(1)).delete(this.role);

	}

}
