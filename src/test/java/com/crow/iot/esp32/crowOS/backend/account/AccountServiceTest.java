package com.crow.iot.esp32.crowOS.backend.account;

import com.crow.iot.esp32.crowOS.backend.commons.ResourceNotFoundException;
import com.crow.iot.esp32.crowOS.backend.security.MissingPermissionException;
import com.crow.iot.esp32.crowOS.backend.security.SecurityTools;
import com.crow.iot.esp32.crowOS.backend.security.role.Role;
import com.crow.iot.esp32.crowOS.backend.security.role.permission.Privilege;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * @author : error23
 * Created : 11/06/2020
 */
@SpringBootTest
class AccountServiceTest {

	@MockBean
	AccountDao accountDao;

	@InjectMocks
	@Autowired
	AccountService accountService;

	AccountDto dto;
	Account account;
	Account connectedAccount;
	AccountPermissionHolder permissionHolder;

	@BeforeEach
	void setUp() {

		this.permissionHolder = new AccountPermissionHolder();

		Role role = new Role();
		role.setPriority(1);
		role.setName("accountRole");
		role.setRoot(false);

		this.account = new Account();
		this.account.setId(1L);
		this.account.setEnabled(true);
		this.account.setFirstName("igor");
		this.account.setLastName("Rajic");
		this.account.setEmail("error23.d@gmail.com");
		this.account.setPassword("test");
		this.account.setLocale(Locale.FRENCH);
		Mockito.when(this.accountDao.get(1L)).thenReturn(this.account);
		Mockito.when(this.accountDao.get("error23.d@gmail.com")).thenReturn(this.account);

		this.connectedAccount = new Account();
		this.connectedAccount.setId(2L);
		this.connectedAccount.setEnabled(true);
		this.connectedAccount.setFirstName("error23");
		this.connectedAccount.setLastName("rolly");
		this.connectedAccount.setEmail("rolly.d@gmail.com");
		this.connectedAccount.setPassword("test");
		this.connectedAccount.setLocale(Locale.FRENCH);
		this.connectedAccount.setRoles(new ArrayList<>(List.of(role)));
		this.connectedAccount.setOwner(this.connectedAccount);
		Mockito.when(this.accountDao.get(2L)).thenReturn(this.connectedAccount);
		Mockito.when(this.accountDao.get("rolly.d@gmail.com")).thenReturn(this.connectedAccount);

		this.dto = new AccountDto();
		this.dto.setEnabled(true);
		this.dto.setFirstName("igor");
		this.dto.setLastName("Rajic");
		this.dto.setEmail("error23.d@gmail.com");
		this.dto.setPassword("test");
		this.dto.setLocale(Locale.FRENCH);

	}

	@Test
	void whenGet_thanFail() {

		assertThrows(AuthenticationCredentialsNotFoundException.class, () -> this.accountService.get(this.connectedAccount.getId()));
		SecurityTools.setConnectedAccount(this.connectedAccount);
		assertThrows(MissingPermissionException.class, () -> this.accountService.get(this.connectedAccount.getId()));
		assertThrows(MissingPermissionException.class, () -> this.accountService.get(this.account.getId()));
		assertThrows(ResourceNotFoundException.class, () -> this.accountService.get(10000L));
		assertThat(this.accountService.get("10000L")).isNull();

		this.connectedAccount.getRoles().get(0).setPermissions(new ArrayList<>(List.of(this.permissionHolder.toOwn(this.permissionHolder.getReadAccount()))));
		assertThrows(MissingPermissionException.class, () -> this.accountService.get(this.account.getId()));

		this.connectedAccount.getRoles().get(0).getPermissions().get(0).getPrivileges().add(Privilege.READ);
		assertThrows(MethodArgumentNotValidException.class, () -> this.accountService.get((Long) null));

	}

	@Test
	void whenGet_thanSuccess() throws MethodArgumentNotValidException {

		assertThat(this.accountService.get(this.account.getEmail())).isEqualToComparingFieldByField(this.account);

		SecurityTools.setConnectedAccount(this.connectedAccount);
		this.connectedAccount.getRoles().get(0).setPermissions(new ArrayList<>(List.of(this.permissionHolder.toOwn(this.permissionHolder.getReadAccount()))));
		assertThat(this.accountService.get(this.connectedAccount.getId())).isEqualToComparingFieldByField(this.connectedAccount);

		this.connectedAccount.getRoles().get(0).getPermissions().get(0).getPrivileges().add(Privilege.READ);
		assertThat(this.accountService.get(this.account.getId())).isEqualToComparingFieldByField(this.account);

	}

	@Test
	void whenCreate_thanFail() {

		assertThrows(AuthenticationCredentialsNotFoundException.class, () -> this.accountService.create(this.dto));

		SecurityTools.setConnectedAccount(this.connectedAccount);
		assertThrows(MissingPermissionException.class, () -> this.accountService.create(this.dto));

		this.connectedAccount.getRoles().get(0).setPermissions(new ArrayList<>(List.of(this.permissionHolder.getCreateAccount())));
		assertThrows(MissingPermissionException.class, () -> this.accountService.create(this.dto));

		this.connectedAccount.getRoles().get(0).getPermissions().add(this.permissionHolder.getUpdateAccountPassword());
		assertThrows(MissingPermissionException.class, () -> this.accountService.create(this.dto));

		this.connectedAccount.getRoles().get(0).setPermissions(new ArrayList<>(List.of(this.permissionHolder.getCreateAccount(), this.permissionHolder.getUpdateAccountEnabled())));
		assertThrows(MissingPermissionException.class, () -> this.accountService.create(this.dto));
	}

	@Test
	void whenCreate_thenSuccess() {

		SecurityTools.setConnectedAccount(this.connectedAccount);

		this.connectedAccount.getRoles().get(0).setPermissions(new ArrayList<>(List.of(this.permissionHolder.getCreateAccount(), this.permissionHolder.getUpdateAccountPassword(), this.permissionHolder.getUpdateAccountEnabled())));
		assertThat(this.accountService.create(this.dto)).isNotNull();

	}

	@Test
	void whenUpdate_thenFail() {

		this.dto.setFirstName("test");
		this.dto.setPassword("tata");
		this.dto.setEnabled(false);

		assertThrows(AuthenticationCredentialsNotFoundException.class, () -> this.accountService.update(this.account, this.dto));

		SecurityTools.setConnectedAccount(this.connectedAccount);
		assertThrows(MissingPermissionException.class, () -> this.accountService.update(this.account, this.dto));

		this.connectedAccount.getRoles().get(0).setPermissions(new ArrayList<>(List.of(this.permissionHolder.toOwn(this.permissionHolder.getUpdateAccount()))));
		assertThrows(MissingPermissionException.class, () -> this.accountService.update(this.account, this.dto));

		this.connectedAccount.getRoles().get(0).getPermissions().get(0).getPrivileges().add(Privilege.UPDATE);
		assertThrows(MissingPermissionException.class, () -> this.accountService.update(this.account, this.dto));

		this.connectedAccount.getRoles().get(0).getPermissions().add(this.permissionHolder.getUpdateAccountPassword());
		assertThrows(MissingPermissionException.class, () -> this.accountService.update(this.account, this.dto));

	}

	@Test
	void whenUpdate_thanSuccess() {

		this.dto = new AccountDto();

		this.dto.setFirstName("test");

		SecurityTools.setConnectedAccount(this.connectedAccount);

		this.connectedAccount.getRoles().get(0).setPermissions(new ArrayList<>(List.of(this.permissionHolder.toOwn(this.permissionHolder.getUpdateAccount()))));
		this.accountService.update(this.connectedAccount, this.dto);
		assertThat(this.connectedAccount.getFirstName()).isEqualTo(this.dto.getFirstName());

		this.dto.setPassword("tata");
		this.connectedAccount.getRoles().get(0).getPermissions().add(this.permissionHolder.getUpdateAccountPassword());
		this.accountService.update(this.connectedAccount, this.dto);
		assertThat(this.connectedAccount.getPassword()).isEqualTo(this.dto.getPassword());

		this.dto.setEnabled(false);
		this.connectedAccount.getRoles().get(0).getPermissions().add(this.permissionHolder.getUpdateAccountEnabled());
		this.accountService.update(this.connectedAccount, this.dto);
		assertThat(this.connectedAccount.isEnabled()).isEqualTo(this.dto.getEnabled());

		this.connectedAccount.getRoles().get(0).setPermissions(new ArrayList<>(List.of(this.permissionHolder.toOwn(this.permissionHolder.getUpdateAccount()))));
		this.connectedAccount.getRoles().get(0).getPermissions().get(0).getPrivileges().add(Privilege.UPDATE);
		this.connectedAccount.getRoles().get(0).getPermissions().add(this.permissionHolder.getUpdateAccountPassword());
		this.connectedAccount.getRoles().get(0).getPermissions().add(this.permissionHolder.getUpdateAccountEnabled());

		this.dto.setFirstName("test2");
		this.dto.setPassword("tata2");
		this.dto.setEnabled(true);

		this.accountService.update(this.account, this.dto);

		assertThat(this.account.getFirstName()).isEqualTo(this.dto.getFirstName());
		assertThat(this.account.getPassword()).isEqualTo(this.dto.getPassword());
		this.connectedAccount.getRoles().get(0).getPermissions().add(this.permissionHolder.getUpdateAccountEnabled());

	}

	@Test
	void whenDeleting_thenFail() {

		assertThrows(AuthenticationCredentialsNotFoundException.class, () -> this.accountService.delete(this.account));

		SecurityTools.setConnectedAccount(this.connectedAccount);
		assertThrows(MissingPermissionException.class, () -> this.accountService.delete(this.account));

		this.connectedAccount.getRoles().get(0).setPermissions(new ArrayList<>(List.of(this.permissionHolder.toOwn(this.permissionHolder.getDeleteAccount()))));
		assertThrows(MissingPermissionException.class, () -> this.accountService.delete(this.account));

	}

	@Test
	void whenDeleting_thenSuccess() {

		SecurityTools.setConnectedAccount(this.connectedAccount);
		this.connectedAccount.getRoles().get(0).setPermissions(new ArrayList<>(List.of(this.permissionHolder.toOwn(this.permissionHolder.getDeleteAccount()))));
		this.accountService.delete(this.connectedAccount);
		verify(this.accountDao, times(1)).delete(this.connectedAccount);

		this.connectedAccount.getRoles().get(0).getPermissions().get(0).getPrivileges().add(Privilege.DELETE);
		this.accountService.delete(this.account);
		verify(this.accountDao, times(1)).delete(this.account);

	}
}
