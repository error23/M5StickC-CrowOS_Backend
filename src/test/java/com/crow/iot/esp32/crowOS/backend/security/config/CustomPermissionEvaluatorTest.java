package com.crow.iot.esp32.crowOS.backend.security.config;

import com.crow.iot.esp32.crowOS.backend.account.Account;
import com.crow.iot.esp32.crowOS.backend.account.AccountPermissionHolder;
import com.crow.iot.esp32.crowOS.backend.security.MissingPermissionException;
import com.crow.iot.esp32.crowOS.backend.security.role.Role;
import com.crow.iot.esp32.crowOS.backend.security.role.permission.Privilege;
import com.crow.iot.esp32.crowOS.backend.security.role.permission.SecuredResource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author : error23
 * Created : 13/06/2020
 */
@ExtendWith (MockitoExtension.class)
class CustomPermissionEvaluatorTest {

	CustomPermissionEvaluator customPermissionEvaluator;

	@Mock
	SecurityDetails securityDetails;

	@InjectMocks
	UsernamePasswordAuthenticationToken auth;

	Account connectedAccount;

	@BeforeEach
	void setUp() {

		this.customPermissionEvaluator = new CustomPermissionEvaluator();

		this.connectedAccount = new Account();
		this.connectedAccount.setId(1L);
		this.connectedAccount.setEnabled(true);
		this.connectedAccount.setFirstName("igor");
		this.connectedAccount.setLastName("Rajic");
		this.connectedAccount.setEmail("error23.d@gmail.com");
		this.connectedAccount.setPassword("test");
		this.connectedAccount.setLocale(Locale.FRENCH);

		Role role = new Role();
		role.setId(1L);
		role.setName("testA");
		role.setRoot(false);
		role.setPriority(1);
		this.connectedAccount.setRoles(List.of(role));

		Mockito.when(this.securityDetails.getAccount()).thenReturn(this.connectedAccount);

	}

	private Account create(int id) {

		Account account = new Account();
		account.setId((long) id);
		account.setEnabled(true);
		account.setFirstName("test");
		account.setLastName("testLast");
		account.setEmail("test@gmail.com");
		account.setPassword("test");
		account.setOwner(this.connectedAccount);
		account.setLocale(Locale.FRENCH);

		return account;
	}

	@Test
	void when_hasPermission_thanFail() {

		assertThrows(MissingPermissionException.class, () ->
			this.customPermissionEvaluator.hasPermission(
				this.auth,
				null,
				SecuredResource.ACCOUNT.toString(),
				Privilege.READ.toString()));

		// String
		assertThrows(MissingPermissionException.class, () ->
			this.customPermissionEvaluator.hasPermission(
				this.auth,
				SecuredResource.ACCOUNT.toString(),
				Privilege.READ.toString()));

		// Collection
		List<Account> collection = new ArrayList<>();
		for (int i = 0; i < 10; i++) {

			collection.add(this.create(i));
		}

		assertThrows(MissingPermissionException.class, () ->
			this.customPermissionEvaluator.hasPermission(
				this.auth,
				collection,
				Privilege.READ.toString()));

		// Object
		assertThrows(MissingPermissionException.class, () ->
			this.customPermissionEvaluator.hasPermission(
				this.auth,
				this.create(2),
				Privilege.READ.toString()));

		// Other "error case"
		assertThat(this.customPermissionEvaluator.hasPermission(this.auth, 2D, Privilege.READ)).isFalse();

	}

	@Test
	void when_hasPermission_thanSuccess() {

		AccountPermissionHolder permissions = new AccountPermissionHolder();
		this.connectedAccount.getRoles().get(0).setPermissions(List.of(permissions.getReadAccount()));

		assertThat(this.customPermissionEvaluator.hasPermission(this.auth, null, SecuredResource.ACCOUNT.toString(), Privilege.READ.toString())).isTrue();

		// String
		assertThat(this.customPermissionEvaluator.hasPermission(this.auth, SecuredResource.ACCOUNT.toString(), Privilege.READ.toString())).isTrue();

		// Collection
		List<Account> collection = new ArrayList<>();
		for (int i = 0; i < 10; i++) {

			collection.add(this.create(i));
		}

		assertThat(this.customPermissionEvaluator.hasPermission(this.auth, collection, Privilege.READ.toString())).isTrue();

		// Object
		assertThat(this.customPermissionEvaluator.hasPermission(this.auth, this.create(2), Privilege.READ.toString())).isTrue();

	}

	@Test
	void when_hasPermissionWithOwnPermission_thanSuccess() {

		AccountPermissionHolder permissions = new AccountPermissionHolder();
		this.connectedAccount.getRoles().get(0).setPermissions(List.of(permissions.toOwn(permissions.getReadAccount())));

		// Collection
		List<Account> collection = new ArrayList<>();
		for (int i = 0; i < 10; i++) {

			collection.add(this.create(i));
		}

		assertThat(this.customPermissionEvaluator.hasPermission(this.auth, collection, Privilege.READ.toString())).isTrue();

		// Object
		assertThat(this.customPermissionEvaluator.hasPermission(this.auth, this.create(2), Privilege.READ.toString())).isTrue();

	}

}
