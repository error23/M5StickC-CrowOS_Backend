package com.crow.iot.esp32.crowOS.backend.account;

import com.crow.iot.esp32.crowOS.backend.commons.CommonTools;
import com.crow.iot.esp32.crowOS.backend.security.role.Role;
import com.crow.iot.esp32.crowOS.backend.security.role.RoleDao;
import com.crow.iot.esp32.crowOS.backend.security.role.permission.Permission;
import com.crow.iot.esp32.crowOS.backend.security.role.permission.Privilege;
import com.crow.iot.esp32.crowOS.backend.security.role.permission.SecuredResource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author : error23
 * Created : 11/06/2020
 */
@SpringBootTest
@Transactional
class AccountTest {

	@Autowired
	RoleDao roleDao;

	@Autowired
	AccountDao accountDao;

	Account account;

	Role roleA;

	Role roleB;

	@BeforeEach
	void setUp() {

		this.roleA = new Role();
		this.roleA.setId(1L);
		this.roleA.setName("testA");
		this.roleA.setRoot(false);
		this.roleA.setPriority(1);

		Permission pA = new Permission();
		pA.setPrivileges(List.of(Privilege.READ, Privilege.CREATE, Privilege.DELETE));
		pA.setSecuredResource(SecuredResource.ROLE);

		this.roleA.setPermissions(new ArrayList<>(List.of(pA)));
		this.roleDao.save(this.roleA);

		this.roleB = new Role();
		this.roleB.setId(2L);
		this.roleB.setName("testB");
		this.roleB.setRoot(false);
		this.roleB.setPriority(2);

		Permission pB = new Permission();
		pB.setPrivileges(List.of(Privilege.READ));
		pB.setSecuredResource(SecuredResource.STACK_TRACE);

		this.roleB.setPermissions(new ArrayList<>(List.of(pB)));
		this.roleDao.save(this.roleB);

		this.account = new Account();
		this.account.setId(1L);
		this.account.setEnabled(true);
		this.account.setFirstName("igor");
		this.account.setLastName("Rajic");
		this.account.setEmail("error23.d@gmail.com");
		this.account.setPassword("test");
		this.account.setLocale(Locale.FRENCH);
		this.account.setRoles(List.of(this.roleA, this.roleB));
		this.accountDao.save(this.account);

		CommonTools.invokeMethod(AccountDao.class, this.accountDao, "flushAndClear", null, null);
	}

	@Test
	void whenRetrievingAccountWithRoles_thanSuccess() {

		Account account = this.accountDao.get(this.account.getId());
		assertThat(account.getRoles()).isNotEmpty();
		assertThat(account.getRoles()).hasSize(2);
		assertThat(account.getRoles()).containsExactly(this.roleB, this.roleA);

	}

}
