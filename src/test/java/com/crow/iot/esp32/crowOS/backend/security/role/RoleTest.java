package com.crow.iot.esp32.crowOS.backend.security.role;

import com.crow.iot.esp32.crowOS.backend.security.role.permission.Permission;
import com.crow.iot.esp32.crowOS.backend.security.role.permission.Privilege;
import com.crow.iot.esp32.crowOS.backend.security.role.permission.SecuredResource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author : error23
 * Created : 13/06/2020
 */
@SpringBootTest
@Transactional
class RoleTest {

	Role role;

	@Autowired
	RoleDao roleDao;

	RolePermissionHolder rolePermissionHolder;

	@BeforeEach
	void setUp() {

		this.rolePermissionHolder = new RolePermissionHolder();

		this.role = new Role();
		this.role.setId(1L);
		this.role.setName("test");
		this.role.setPriority(1);

	}

	@Test
	void whenRetrievingRoleWithPermissions_thanSuccess() {

		List<Permission> permissions = new ArrayList<>(List.of(this.rolePermissionHolder.getCreateRole(), this.rolePermissionHolder.getDeleteRole()));

		this.role.setPermissions(permissions);
		this.roleDao.save(this.role);

		Role role = this.roleDao.get(this.role.getId());
		assertThat(role.getPermissions()).isNotEmpty();
		assertThat(role.getPermissions()).hasSize(2);
		assertThat(role.getPermissions()).containsExactly(permissions.toArray(new Permission[0]));

	}

	@Test
	void whenCanWithRoot_thanReturnTrue() {

		this.role.setRoot(true);
		assertThat(this.role.isRoot()).isTrue();

		for (Map.Entry<SecuredResource, Privilege> entry : this.generateRootPermissions().entrySet()) {
			assertThat(this.role.can(entry.getValue(), entry.getKey())).isTrue();
		}
	}

	@Test
	void whenCanWithoutPermissions_thanReturnFalse() {

		for (Map.Entry<SecuredResource, Privilege> entry : this.generateRootPermissions().entrySet()) {
			assertThat(this.role.can(entry.getValue(), entry.getKey())).isFalse();
		}
	}

	@Test
	void whenRoleHasListOfPermissions_thanSearchPermissionAndReturnResult() {

		Permission permission = new Permission();
		permission.setSecuredResource(SecuredResource.ROLE);
		permission.setPrivileges(List.of(Privilege.CREATE, Privilege.DELETE, Privilege.UPDATE));
		List<Permission> permissions = List.of(permission);
		this.role.setPermissions(permissions);

		assertThat(this.role.can(Privilege.READ, SecuredResource.ROLE)).isFalse();
		assertThat(this.role.can(Privilege.CREATE, SecuredResource.ROLE)).isTrue();
		assertThat(this.role.can(Privilege.DELETE, SecuredResource.ROLE)).isTrue();
		assertThat(this.role.can(Privilege.UPDATE, SecuredResource.ROLE)).isTrue();
		assertThat(this.role.can(Privilege.UPDATE, SecuredResource.ACCOUNT)).isFalse();

	}

	private HashMap<SecuredResource, Privilege> generateRootPermissions() {

		HashMap<SecuredResource, Privilege> roles = new HashMap<>();

		for (SecuredResource securedResource : SecuredResource.values()) {
			for (Privilege privilege : Privilege.values()) {
				roles.put(securedResource, privilege);
			}
		}

		return roles;
	}
}
