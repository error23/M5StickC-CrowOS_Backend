package com.crow.iot.esp32.crowOS.backend.security.role;

import com.crow.iot.esp32.crowOS.backend.account.Account;
import com.crow.iot.esp32.crowOS.backend.security.MissingPermissionException;
import com.crow.iot.esp32.crowOS.backend.security.SecurityTools;
import com.crow.iot.esp32.crowOS.backend.security.role.permission.Permission;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author : error23
 * Created : 13/06/2020
 */
@SpringBootTest
@Slf4j
class RoleMapperTest {

	@Autowired
	RoleMapper mapper;

	RolePermissionHolder permissionHolder;

	Role role;

	RoleDto roleDto;

	Account connectedAccount;

	@BeforeEach
	void setUp() {

		this.permissionHolder = new RolePermissionHolder();
		List<Permission> permissions = new ArrayList<>(List.of(this.permissionHolder.getCreateRole(), this.permissionHolder.getReadRole()));

		this.role = new Role();
		this.role.setId(1L);
		this.role.setName("testA");
		this.role.setRoot(false);
		this.role.setPriority(1);
		this.role.setPermissions(permissions);

		this.roleDto = new RoleDto();
		this.roleDto.setId(1L);
		this.roleDto.setName("testA");
		this.roleDto.setPriority(1);
		this.roleDto.setPermissions(permissions);

		this.connectedAccount = new Account();
		this.connectedAccount.setId(1L);
		this.connectedAccount.setRoles(List.of(this.role));

	}

	@Test
	void whenConvertingToDto_thanSuccess() {

		this.roleDto.setRoot(false);
		RoleDto dto = this.mapper.toDto(this.role);
		assertThat(dto.toString()).isEqualTo(this.roleDto.toString());
	}

	@Test
	void whenConvertingToEntity_thanFail() {

		this.roleDto.setRoot(true);
		MissingPermissionException mpe = assertThrows(MissingPermissionException.class, () -> this.mapper.toEntity(this.roleDto));
		log.debug("Missing permission exception OK :", mpe);

		SecurityTools.setConnectedAccount(this.connectedAccount);

		mpe = assertThrows(MissingPermissionException.class, () -> this.mapper.toEntity(this.roleDto));
		log.debug("Missing permission exception OK :", mpe);
	}

	@Test
	void whenConvertingToEntity_thanSuccess() {

		Role role = this.mapper.toEntity(this.roleDto);
		assertThat(role).isEqualToComparingFieldByField(this.role);

		SecurityTools.setConnectedAccount(this.connectedAccount);

		this.role.setRoot(true);
		this.roleDto.setRoot(true);

		role = this.mapper.toEntity(this.roleDto);
		assertThat(role).isEqualToComparingFieldByField(this.role);

	}

	@Test
	void whenMerging_thanFail() {

		this.roleDto.setRoot(true);
		MissingPermissionException mpe = assertThrows(MissingPermissionException.class, () -> this.mapper.merge(this.roleDto, this.role));
		log.debug("Missing permission exception OK :", mpe);

		SecurityTools.setConnectedAccount(this.connectedAccount);

		mpe = assertThrows(MissingPermissionException.class, () -> this.mapper.merge(this.roleDto, this.role));
		log.debug("Missing permission exception OK :", mpe);

	}

	@Test
	void whenMerging_thanSuccess() {

		Role role = this.mapper.merge(this.roleDto, new Role());
		assertThat(role).isEqualToComparingFieldByField(this.role);

		SecurityTools.setConnectedAccount(this.connectedAccount);

		this.roleDto.setRoot(true);
		this.role.setRoot(true);

		role = this.mapper.merge(this.roleDto, new Role());
		assertThat(role).isEqualToComparingFieldByField(this.role);

	}

}
