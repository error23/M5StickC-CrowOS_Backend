package com.crow.iot.esp32.crowOS.backend.security.role;

import com.crow.iot.esp32.crowOS.backend.account.Account;
import com.crow.iot.esp32.crowOS.backend.security.SecurityTools;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author : error23
 * Created : 14/06/2020
 */
@SpringBootTest
@AutoConfigureMockMvc
class RoleEndpointTest {

	@MockBean
	RoleService roleService;

	@Autowired
	MockMvc mvc;

	Account connectedAccount;

	Role role;

	RoleDto dto;

	@BeforeEach
	void setUp() throws MethodArgumentNotValidException {

		RolePermissionHolder permissionHolder = new RolePermissionHolder();

		this.role = new Role();
		this.role.setId(1L);
		this.role.setName("testA");
		this.role.setRoot(true);
		this.role.setPriority(1);
		this.role.setPermissions(List.of(permissionHolder.getCreateRole(), permissionHolder.getUpdateRole(), permissionHolder.getDeleteRole()));

		this.dto = new RoleDto();
		this.dto.setId(1L);
		this.dto.setName("testA");
		this.dto.setRoot(true);
		this.dto.setPriority(1);
		this.dto.setPermissions(List.of(permissionHolder.getCreateRole(), permissionHolder.getUpdateRole(), permissionHolder.getDeleteRole()));

		this.connectedAccount = new Account();
		this.connectedAccount.setId(1L);
		this.connectedAccount.setEnabled(true);
		this.connectedAccount.setFirstName("error23");
		this.connectedAccount.setLastName("rolly");
		this.connectedAccount.setEmail("error23.d@gmail.com");
		this.connectedAccount.setPassword("test");
		this.connectedAccount.setLocale(Locale.FRENCH);
		this.connectedAccount.setRoles(new ArrayList<>(List.of(this.role)));
		this.connectedAccount.setOwner(this.connectedAccount);
		SecurityTools.setConnectedAccount(this.connectedAccount);

		Mockito.when(this.roleService.get(1L)).thenReturn(this.role);
		Mockito.when(this.roleService.create(any())).thenReturn(this.role);
		Mockito.when(this.roleService.update(any(), any())).thenReturn(this.role);

	}

	@Test
	void whenGet_thanSuccess() throws Exception {

		this.mvc.perform(get("/role/{id}", 1L))
		        .andDo(log())
		        .andExpect(status().isOk())
		        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
		        .andExpect(content().encoding(StandardCharsets.UTF_8.name()))
		        .andExpect(content().json(this.dto.toString(), true));

		verify(this.roleService, times(1)).get(1L);
	}

	@Test
	void whenCreate_thanSuccess() throws Exception {

		this.dto.setId(null);

		this.mvc.perform(post("/role")
			                 .contentType(MediaType.APPLICATION_JSON_VALUE)
			                 .content(this.dto.toString())
		                )
		        .andDo(log())
		        .andExpect(status().isCreated())
		        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
		        .andExpect(content().encoding(StandardCharsets.UTF_8.name()))
		        .andExpect(content().json(this.dto.toString(), false))
		        .andExpect(jsonPath("$.id").value(this.role.getId()));

		verify(this.roleService, times(1)).create(any());
	}

	@Test
	void whenUpdate_thanSuccess() throws Exception {

		this.mvc.perform(patch("/role")
			                 .contentType(MediaType.APPLICATION_JSON_VALUE)
			                 .content(this.dto.toString())
		                )
		        .andDo(log())
		        .andExpect(status().isAccepted())
		        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
		        .andExpect(content().encoding(StandardCharsets.UTF_8.name()))
		        .andExpect(content().json(this.dto.toString(), true));

		verify(this.roleService, times(1)).update(any(), any());

	}

	@Test
	void whenDeleting_thanSuccess() throws Exception {

		this.mvc.perform(delete("/role/{id}", 1L))
		        .andDo(log())
		        .andExpect(status().isAccepted())
		        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
		        .andExpect(content().encoding(StandardCharsets.UTF_8.name()))
		        .andExpect(content().json(this.dto.toString(), true));

		verify(this.roleService, times(1)).delete(any());
	}
}
