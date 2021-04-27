package com.crow.iot.esp32.crowOS.backend.account;

import com.crow.iot.esp32.crowOS.backend.security.SecurityTools;
import com.crow.iot.esp32.crowOS.backend.security.role.Role;
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
 * Created : 11/06/2020
 */
@SpringBootTest
@AutoConfigureMockMvc
class AccountEndpointTest {

	@MockBean
	AccountService accountService;

	@Autowired
	MockMvc mvc;

	Account connectedAccount;

	Account account;

	AccountDto dto;

	@BeforeEach
	void setUp() throws MethodArgumentNotValidException {

		Role role = new Role();
		role.setId(1L);
		role.setName("testA");
		role.setRoot(true);
		role.setPriority(1);

		this.connectedAccount = new Account();
		this.connectedAccount.setId(1L);
		this.connectedAccount.setEnabled(true);
		this.connectedAccount.setFirstName("error23");
		this.connectedAccount.setLastName("rolly");
		this.connectedAccount.setEmail("error23.d@gmail.com");
		this.connectedAccount.setPassword("test");
		this.connectedAccount.setLocale(Locale.FRENCH);
		this.connectedAccount.setRoles(new ArrayList<>(List.of(role)));
		this.connectedAccount.setOwner(this.connectedAccount);
		Mockito.when(this.accountService.get(1L)).thenReturn(this.connectedAccount);
		SecurityTools.setConnectedAccount(this.connectedAccount);

		this.account = new Account();
		this.account.setId(2L);
		this.account.setEnabled(true);
		this.account.setFirstName("igor");
		this.account.setLastName("Rajic");
		this.account.setEmail("error23.d@gmail.com");
		this.account.setPassword("test");
		this.account.setLocale(Locale.FRENCH);
		Mockito.when(this.accountService.get(2L)).thenReturn(this.account);

		this.dto = new AccountDto();
		this.dto.setId(2L);
		this.dto.setEnabled(true);
		this.dto.setFirstName("igor");
		this.dto.setLastName("Rajic");
		this.dto.setEmail("error23.d@gmail.com");
		this.dto.setPassword("test");
		this.dto.setLocale(Locale.FRENCH);

		Mockito.when(this.accountService.create(any())).thenReturn(this.account);
		Mockito.when(this.accountService.update(any(), any())).thenReturn(this.account);

	}

	@Test
	void whenGet_thanSuccess() throws Exception {

		this.mvc.perform(get("/account/{id}", 2L))
		        .andDo(log())
		        .andExpect(status().isOk())
		        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
		        .andExpect(content().encoding(StandardCharsets.UTF_8.name()))
		        .andExpect(content().json(this.dto.toString(), true));

		verify(this.accountService, times(1)).get(2L);
	}

	@Test
	void whenCreate_thanSuccess() throws Exception {

		this.dto.setId(null);

		this.mvc.perform(post("/account")
			                 .contentType(MediaType.APPLICATION_JSON_VALUE)
			                 .content(this.dto.toString())
		                )
		        .andDo(log())
		        .andExpect(status().isCreated())
		        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
		        .andExpect(content().encoding(StandardCharsets.UTF_8.name()))
		        .andExpect(content().json(this.dto.toString(), false))
		        .andExpect(jsonPath("$.id").value(this.account.getId()));

		verify(this.accountService, times(1)).create(any());
	}

	@Test
	void whenUpdate_thanSuccess() throws Exception {

		this.mvc.perform(patch("/account")
			                 .contentType(MediaType.APPLICATION_JSON_VALUE)
			                 .content(this.dto.toString())
		                )
		        .andDo(log())
		        .andExpect(status().isAccepted())
		        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
		        .andExpect(content().encoding(StandardCharsets.UTF_8.name()))
		        .andExpect(content().json(this.dto.toString(), true));

		verify(this.accountService, times(1)).update(any(), any());

	}

	@Test
	void whenDeleting_thanSuccess() throws Exception {

		this.mvc.perform(delete("/account/{id}", 2L))
		        .andDo(log())
		        .andExpect(status().isAccepted())
		        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
		        .andExpect(content().encoding(StandardCharsets.UTF_8.name()))
		        .andExpect(content().json(this.dto.toString(), true));

		verify(this.accountService, times(1)).delete(any());
	}
}
