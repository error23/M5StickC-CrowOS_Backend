package com.crow.iot.esp32.crowOS.backend.commons.architecture;

import com.crow.iot.esp32.crowOS.backend.account.Account;
import com.crow.iot.esp32.crowOS.backend.account.AccountDao;
import com.crow.iot.esp32.crowOS.backend.account.AccountDto;
import com.crow.iot.esp32.crowOS.backend.security.SecurityTools;
import com.crow.iot.esp32.crowOS.backend.security.role.Role;
import com.crow.iot.esp32.crowOS.backend.security.role.permission.Permission;
import com.crow.iot.esp32.crowOS.backend.security.role.permission.Privilege;
import com.crow.iot.esp32.crowOS.backend.security.role.permission.SecuredResource;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author : error23
 * Created : 12/06/2020
 */
@SpringBootTest
@AutoConfigureMockMvc
class AbstractEndpointTest {

	@Autowired
	MockMvc mvc;

	@MockBean
	AccountDao accountDao;

	Account connectedAccount;

	Role role;

	@BeforeEach
	void setUp() {

		this.role = new Role();
		this.role.setRoot(true);

		this.connectedAccount = new Account();
		this.connectedAccount.setId(1L);
		this.connectedAccount.setEnabled(true);
		this.connectedAccount.setFirstName("error23");
		this.connectedAccount.setLastName("rolly");
		this.connectedAccount.setEmail("error23.d@gmail.com");
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		this.connectedAccount.setPassword(encoder.encode("test"));
		this.connectedAccount.setLocale(Locale.FRENCH);

		Mockito.when(this.accountDao.get("error23.d@gmail.com")).thenReturn(this.connectedAccount);

	}

	@Test
	void whenResourceNotFound_thanFailWithSpecificMessages() throws Exception {

		SecurityTools.setConnectedAccount(this.connectedAccount);

		this.mvc.perform(get("/resourceNotFound"))
		        .andDo(log())
		        .andExpect(status().isNotFound())
		        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
		        .andExpect(content().encoding(StandardCharsets.UTF_8.name()))
		        .andExpect(jsonPath("$.error").value("RESOURCE_NOT_FOUND"))
		        .andExpect(jsonPath("$.detailsHumanReadable").value("Resource: test [1] not found!"))
		        .andExpect(jsonPath("$.stackTrace").doesNotExist());

		this.mvc.perform(get("/resourceNotFound").param("lc", "fr"))
		        .andDo(log())
		        .andExpect(status().isNotFound())
		        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
		        .andExpect(content().encoding(StandardCharsets.UTF_8.name()))
		        .andExpect(jsonPath("$.error").value("RESOURCE_NOT_FOUND"))
		        .andExpect(jsonPath("$.detailsHumanReadable").value("Resource : test [1] n'as pas ete trouvée !"))
		        .andExpect(jsonPath("$.stackTrace").doesNotExist());

		this.connectedAccount.setRoles(List.of(this.role));
		this.mvc.perform(get("/resourceNotFound"))
		        .andDo(log())
		        .andExpect(status().isNotFound())
		        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
		        .andExpect(content().encoding(StandardCharsets.UTF_8.name()))
		        .andExpect(jsonPath("$.error").value("RESOURCE_NOT_FOUND"))
		        .andExpect(jsonPath("$.detailsHumanReadable").value("Resource: test [1] not found!"))
		        .andExpect(jsonPath("$.stackTrace").exists());

	}

	@Test
	void whenResourceNotFoundWithoutIds_thanFailWithSpecificMessage() throws Exception {

		SecurityTools.setConnectedAccount(this.connectedAccount);

		this.mvc.perform(get("/resourceNotFoundNoIds"))
		        .andDo(log())
		        .andExpect(status().isNotFound())
		        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
		        .andExpect(content().encoding(StandardCharsets.UTF_8.name()))
		        .andExpect(jsonPath("$.error").value("RESOURCE_NOT_FOUND"))
		        .andExpect(jsonPath("$.detailsHumanReadable").value("Resource: test not found!"))
		        .andExpect(jsonPath("$.stackTrace").doesNotExist());

		this.mvc.perform(get("/resourceNotFoundNoIds").param("lc", "fr"))
		        .andDo(log())
		        .andExpect(status().isNotFound())
		        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
		        .andExpect(content().encoding(StandardCharsets.UTF_8.name()))
		        .andExpect(jsonPath("$.error").value("RESOURCE_NOT_FOUND"))
		        .andExpect(jsonPath("$.detailsHumanReadable").value("Resource : test n'as pas ete trouvée !"))
		        .andExpect(jsonPath("$.stackTrace").doesNotExist());

		this.connectedAccount.setRoles(List.of(this.role));

		this.mvc.perform(get("/resourceNotFoundNoIds").param("lc", "fr"))
		        .andDo(log())
		        .andExpect(status().isNotFound())
		        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
		        .andExpect(content().encoding(StandardCharsets.UTF_8.name()))
		        .andExpect(jsonPath("$.error").value("RESOURCE_NOT_FOUND"))
		        .andExpect(jsonPath("$.detailsHumanReadable").value("Resource : test n'as pas ete trouvée !"))
		        .andExpect(jsonPath("$.stackTrace").exists());

	}

	@Test
	void whenBadRequest_thanFail() throws Exception {

		SecurityTools.setConnectedAccount(this.connectedAccount);

		this.mvc.perform(get("/badRequest"))
		        .andDo(log())
		        .andExpect(status().isBadRequest())
		        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
		        .andExpect(content().encoding(StandardCharsets.UTF_8.name()))
		        .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
		        .andExpect(jsonPath("$.detailsHumanReadable").value("400 Bad Request"))
		        .andExpect(jsonPath("$.stackTrace").doesNotExist());

		this.mvc.perform(get("/badRequest").param("lc", "fr"))
		        .andDo(log())
		        .andExpect(status().isBadRequest())
		        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
		        .andExpect(content().encoding(StandardCharsets.UTF_8.name()))
		        .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
		        .andExpect(jsonPath("$.detailsHumanReadable").value("400 Bad Request"))
		        .andExpect(jsonPath("$.stackTrace").doesNotExist());

		this.connectedAccount.setRoles(List.of(this.role));

		this.mvc.perform(get("/badRequest").param("lc", "fr"))
		        .andDo(log())
		        .andExpect(status().isBadRequest())
		        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
		        .andExpect(content().encoding(StandardCharsets.UTF_8.name()))
		        .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
		        .andExpect(jsonPath("$.detailsHumanReadable").value("400 Bad Request"))
		        .andExpect(jsonPath("$.stackTrace").exists());

	}

	@Test
	void badRequestValidationError_thanFail() throws Exception {

		SecurityTools.setConnectedAccount(this.connectedAccount);

		AccountDto dto = new AccountDto();

		this.mvc.perform(post("/badRequestValidationError")
			                 .contentType(MediaType.APPLICATION_JSON_VALUE)
			                 .content(dto.toString()))
		        .andDo(log())
		        .andExpect(status().isBadRequest())
		        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
		        .andExpect(content().encoding(StandardCharsets.UTF_8.name()))
		        .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
		        .andExpect(jsonPath("$.detailsHumanReadable").value(Matchers.startsWith("Input validation failed with 4 errors: ")))
		        .andExpect(jsonPath("$.detailsHumanReadable").value(Matchers.containsString("Field email must not be null ;")))
		        .andExpect(jsonPath("$.detailsHumanReadable").value(Matchers.containsString("Field password must not be null ;")))
		        .andExpect(jsonPath("$.detailsHumanReadable").value(Matchers.containsString("Field firstName must not be null ;")))
		        .andExpect(jsonPath("$.detailsHumanReadable").value(Matchers.containsString("Field lastName must not be null ;")))
		        .andExpect(jsonPath("$.stackTrace").doesNotExist());

		this.mvc.perform(post("/badRequestValidationError")
			                 .contentType(MediaType.APPLICATION_JSON_VALUE)
			                 .param("lc", "fr")
			                 .content(dto.toString()))
		        .andDo(log())
		        .andExpect(status().isBadRequest())
		        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
		        .andExpect(content().encoding(StandardCharsets.UTF_8.name()))
		        .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
		        .andExpect(jsonPath("$.detailsHumanReadable").value(Matchers.startsWith("Validation des entrées a échouée avec 4 erreurs : ")))
		        .andExpect(jsonPath("$.detailsHumanReadable").value(Matchers.containsString("Champ email ne doit pas être nul ;")))
		        .andExpect(jsonPath("$.detailsHumanReadable").value(Matchers.containsString("Champ lastName ne doit pas être nul ;")))
		        .andExpect(jsonPath("$.detailsHumanReadable").value(Matchers.containsString("Champ firstName ne doit pas être nul ;")))
		        .andExpect(jsonPath("$.detailsHumanReadable").value(Matchers.containsString("Champ password ne doit pas être nul ;")))
		        .andExpect(jsonPath("$.stackTrace").doesNotExist());

		this.connectedAccount.setRoles(List.of(this.role));

		this.mvc.perform(post("/badRequestValidationError")
			                 .contentType(MediaType.APPLICATION_JSON_VALUE)
			                 .param("lc", "fr")
			                 .content(dto.toString())).andDo(log())
		        .andExpect(status().isBadRequest())
		        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
		        .andExpect(content().encoding(StandardCharsets.UTF_8.name()))
		        .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
		        .andExpect(jsonPath("$.detailsHumanReadable").value(Matchers.startsWith("Validation des entrées a échouée avec 4 erreurs : ")))
		        .andExpect(jsonPath("$.detailsHumanReadable").value(Matchers.containsString("Champ email ne doit pas être nul ;")))
		        .andExpect(jsonPath("$.detailsHumanReadable").value(Matchers.containsString("Champ lastName ne doit pas être nul ;")))
		        .andExpect(jsonPath("$.detailsHumanReadable").value(Matchers.containsString("Champ firstName ne doit pas être nul ;")))
		        .andExpect(jsonPath("$.detailsHumanReadable").value(Matchers.containsString("Champ password ne doit pas être nul ;")))
		        .andExpect(jsonPath("$.stackTrace").exists());

		dto.setFirstName("igor");

		this.mvc.perform(post("/badRequestValidationError")
			                 .contentType(MediaType.APPLICATION_JSON_VALUE)
			                 .param("lc", "fr")
			                 .content(dto.toString()))
		        .andDo(log())
		        .andExpect(status().isBadRequest())
		        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
		        .andExpect(content().encoding(StandardCharsets.UTF_8.name()))
		        .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
		        .andExpect(jsonPath("$.detailsHumanReadable").value(Matchers.startsWith("Validation des entrées a échouée avec 3 erreurs : ")))
		        .andExpect(jsonPath("$.detailsHumanReadable").value(Matchers.containsString("Champ email ne doit pas être nul ;")))
		        .andExpect(jsonPath("$.detailsHumanReadable").value(Matchers.containsString("Champ lastName ne doit pas être nul ;")))
		        .andExpect(jsonPath("$.detailsHumanReadable").value(Matchers.containsString("Champ password ne doit pas être nul ;"))).andExpect(jsonPath("$.stackTrace").exists());

		dto.setLastName("rajic");
		dto.setEmail("error23.d@gmail.com");

		this.mvc.perform(post("/badRequestValidationError")
			                 .contentType(MediaType.APPLICATION_JSON_VALUE)
			                 .param("lc", "fr")
			                 .content(dto.toString()))
		        .andDo(log())
		        .andExpect(status().isBadRequest())
		        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
		        .andExpect(content().encoding(StandardCharsets.UTF_8.name()))
		        .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
		        .andExpect(jsonPath("$.detailsHumanReadable").value("Validation de l'entrée a échouée avec 1 erreur : Champ password ne doit pas être nul ; "))
		        .andExpect(jsonPath("$.stackTrace").exists());

	}

	@Test
	void whenMissingServletRequestParameterException_thanFail() throws Exception {

		SecurityTools.setConnectedAccount(this.connectedAccount);

		this.mvc.perform(get("/missingParameters"))
		        .andDo(log())
		        .andExpect(status().isBadRequest())
		        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
		        .andExpect(content().encoding(StandardCharsets.UTF_8.name()))
		        .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
		        .andExpect(jsonPath("$.detailsHumanReadable").value("Required Long parameter 'id' is not present"))
		        .andExpect(jsonPath("$.stackTrace").doesNotExist());

		this.mvc.perform(get("/missingParameters").param("lc", "fr"))
		        .andDo(log())
		        .andExpect(status().isBadRequest())
		        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
		        .andExpect(content().encoding(StandardCharsets.UTF_8.name()))
		        .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
		        .andExpect(jsonPath("$.detailsHumanReadable").value("Required Long parameter 'id' is not present"))
		        .andExpect(jsonPath("$.stackTrace").doesNotExist());

		this.connectedAccount.setRoles(List.of(this.role));

		this.mvc.perform(get("/missingParameters").param("lc", "fr"))
		        .andDo(log())
		        .andExpect(status().isBadRequest())
		        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
		        .andExpect(content().encoding(StandardCharsets.UTF_8.name()))
		        .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
		        .andExpect(jsonPath("$.detailsHumanReadable").value("Required Long parameter 'id' is not present"))
		        .andExpect(jsonPath("$.stackTrace").exists());
	}

	@Test
	void whenBadCredentialsNotConnected_thanFail() throws Exception {

		this.mvc.perform(get("/badCredentialsNotConnected"))
		        .andDo(log())
		        .andExpect(status().isUnauthorized())
		        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
		        .andExpect(content().encoding(StandardCharsets.UTF_8.name()))
		        .andExpect(jsonPath("$.error").value("BAD_CREDENTIALS_EXCEPTION"))
		        .andExpect(jsonPath("$.detailsHumanReadable").value("Access is denied"))
		        .andExpect(jsonPath("$.stackTrace").doesNotExist());

		this.mvc.perform(get("/badCredentialsNotConnected").param("lc", "fr"))
		        .andDo(log())
		        .andExpect(status().isUnauthorized())
		        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
		        .andExpect(content().encoding(StandardCharsets.UTF_8.name()))
		        .andExpect(jsonPath("$.error").value("BAD_CREDENTIALS_EXCEPTION"))
		        .andExpect(jsonPath("$.detailsHumanReadable").value("Accès refusé"))
		        .andExpect(jsonPath("$.stackTrace").doesNotExist());

	}

	@Test
	void whenBadCredentialsWrongPassword_thanFail() throws Exception {

		this.mvc.perform(get("/badCredentialsWrongPassword")
			                 .header(HttpHeaders.AUTHORIZATION, "Basic " + HttpHeaders.encodeBasicAuth("error23.d@gmail.com", "test", StandardCharsets.UTF_8))
		                )
		        .andDo(log())
		        .andExpect(status().isOk());

		this.mvc.perform(get("/badCredentialsWrongPassword")
			                 .header(HttpHeaders.AUTHORIZATION, "Basic " + HttpHeaders.encodeBasicAuth("error23.d@gmail.com", "wrong", StandardCharsets.UTF_8))
		                )
		        .andDo(log())
		        .andExpect(status().isUnauthorized());

	}

	@Test
	void whenMissingPermissionException_thanFail() throws Exception {

		SecurityTools.setConnectedAccount(this.connectedAccount);

		this.mvc.perform(get("/missingPermissionException"))
		        .andDo(log())
		        .andExpect(status().isForbidden())
		        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
		        .andExpect(content().encoding(StandardCharsets.UTF_8.name()))
		        .andExpect(jsonPath("$.error").value("MISSING_PERMISSION"))
		        .andExpect(jsonPath("$.stackTrace").doesNotExist());

		this.connectedAccount.setRoles(List.of(this.role));

		this.mvc.perform(get("/missingPermissionException"))
		        .andDo(log())
		        .andExpect(status().isForbidden())
		        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
		        .andExpect(content().encoding(StandardCharsets.UTF_8.name()))
		        .andExpect(jsonPath("$.error").value("MISSING_PERMISSION"))
		        .andExpect(jsonPath("$.stackTrace").exists());

	}

	@Test
	void when_missingPermissionExceptionWithMessage_thanFail() throws Exception {

		SecurityTools.setConnectedAccount(this.connectedAccount);

		this.mvc.perform(get("/missingPermissionExceptionWithMessage"))
		        .andDo(log())
		        .andExpect(status().isForbidden())
		        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
		        .andExpect(content().encoding(StandardCharsets.UTF_8.name()))
		        .andExpect(jsonPath("$.error").value("MISSING_PERMISSION"))
		        .andExpect(jsonPath("$.detailsHumanReadable").value("Account 1 doesn't have root privilege!"))
		        .andExpect(jsonPath("$.stackTrace").doesNotExist());

		this.mvc.perform(get("/missingPermissionExceptionWithMessage").param("lc", "fr"))
		        .andDo(log())
		        .andExpect(status().isForbidden())
		        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
		        .andExpect(content().encoding(StandardCharsets.UTF_8.name()))
		        .andExpect(jsonPath("$.error").value("MISSING_PERMISSION"))
		        .andExpect(jsonPath("$.detailsHumanReadable").value("Account 1 n'as pas de privilege root !"))
		        .andExpect(jsonPath("$.stackTrace").doesNotExist());

		this.connectedAccount.setRoles(List.of(this.role));

		this.mvc.perform(get("/missingPermissionExceptionWithMessage").param("lc", "fr"))
		        .andDo(log())
		        .andExpect(status().isForbidden())
		        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
		        .andExpect(content().encoding(StandardCharsets.UTF_8.name()))
		        .andExpect(jsonPath("$.error").value("MISSING_PERMISSION"))
		        .andExpect(jsonPath("$.detailsHumanReadable").value("Account 1 n'as pas de privilege root !"))
		        .andExpect(jsonPath("$.stackTrace").exists());

	}

	@Test
	void whenMissingPermissionExceptionWithPrivilege_thanFail() throws Exception {

		SecurityTools.setConnectedAccount(this.connectedAccount);

		this.mvc.perform(get("/missingPermissionExceptionWithPrivilege"))
		        .andDo(log())
		        .andExpect(status().isForbidden())
		        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
		        .andExpect(content().encoding(StandardCharsets.UTF_8.name()))
		        .andExpect(jsonPath("$.error").value("MISSING_PERMISSION"))
		        .andExpect(jsonPath("$.detailsHumanReadable").value("Missing READ permission on ACCOUNT!"))
		        .andExpect(jsonPath("$.stackTrace").doesNotExist());

		this.mvc.perform(get("/missingPermissionExceptionWithPrivilege").param("lc", "fr"))
		        .andDo(log())
		        .andExpect(status().isForbidden())
		        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
		        .andExpect(content().encoding(StandardCharsets.UTF_8.name()))
		        .andExpect(jsonPath("$.error").value("MISSING_PERMISSION"))
		        .andExpect(jsonPath("$.detailsHumanReadable").value("Vous n'avez pas de permission READ sur l'object ACCOUNT !"))
		        .andExpect(jsonPath("$.stackTrace").doesNotExist());

		this.connectedAccount.setRoles(List.of(this.role));

		this.mvc.perform(get("/missingPermissionExceptionWithPrivilege").param("lc", "fr"))
		        .andDo(log())
		        .andExpect(status().isForbidden())
		        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
		        .andExpect(content().encoding(StandardCharsets.UTF_8.name()))
		        .andExpect(jsonPath("$.error").value("MISSING_PERMISSION"))
		        .andExpect(jsonPath("$.detailsHumanReadable").value("Vous n'avez pas de permission READ sur l'object ACCOUNT !"))
		        .andExpect(jsonPath("$.stackTrace").exists());

	}

	@Test
	void missingPermissionWithPreAuthorize() throws Exception {

		SecurityTools.setConnectedAccount(this.connectedAccount);

		this.mvc.perform(get("/missingPermissionWithPreAuthorize"))
		        .andDo(log())
		        .andExpect(status().isForbidden())
		        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
		        .andExpect(content().encoding(StandardCharsets.UTF_8.name()))
		        .andExpect(jsonPath("$.error").value("MISSING_PERMISSION"))
		        .andExpect(jsonPath("$.detailsHumanReadable").value("Missing READ permission on ACCOUNT!"))
		        .andExpect(jsonPath("$.stackTrace").doesNotExist());

		this.mvc.perform(get("/missingPermissionWithPreAuthorize").param("lc", "fr"))
		        .andDo(log())
		        .andExpect(status().isForbidden())
		        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
		        .andExpect(content().encoding(StandardCharsets.UTF_8.name()))
		        .andExpect(jsonPath("$.error").value("MISSING_PERMISSION"))
		        .andExpect(jsonPath("$.detailsHumanReadable").value("Vous n'avez pas de permission READ sur l'object ACCOUNT !"))
		        .andExpect(jsonPath("$.stackTrace").doesNotExist());

		this.role.setRoot(false);
		Permission readStackTracePermission = new Permission();
		readStackTracePermission.setPrivileges(List.of(Privilege.READ));
		readStackTracePermission.setSecuredResource(SecuredResource.STACK_TRACE);
		this.role.setPermissions(List.of(readStackTracePermission));

		this.connectedAccount.setRoles(List.of(this.role));

		this.mvc.perform(get("/missingPermissionWithPreAuthorize").param("lc", "fr"))
		        .andDo(log())
		        .andExpect(status().isForbidden())
		        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
		        .andExpect(content().encoding(StandardCharsets.UTF_8.name()))
		        .andExpect(jsonPath("$.error").value("MISSING_PERMISSION"))
		        .andExpect(jsonPath("$.detailsHumanReadable").value("Vous n'avez pas de permission READ sur l'object ACCOUNT !"))
		        .andExpect(jsonPath("$.stackTrace").exists());

	}

	@Test
	void whenJsonException() throws Exception {

		SecurityTools.setConnectedAccount(this.connectedAccount);

		this.mvc.perform(get("/jsonException"))
		        .andDo(log())
		        .andExpect(status().isInternalServerError())
		        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
		        .andExpect(content().encoding(StandardCharsets.UTF_8.name()))
		        .andExpect(jsonPath("$.error").value("INTERNAL_SERVER_ERROR"))
		        .andExpect(jsonPath("$.detailsHumanReadable").value("Simple json exception message"))
		        .andExpect(jsonPath("$.stackTrace").doesNotExist());

		this.connectedAccount.setRoles(List.of(this.role));

		this.mvc.perform(get("/jsonException"))
		        .andDo(log())
		        .andExpect(status().isInternalServerError())
		        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
		        .andExpect(content().encoding(StandardCharsets.UTF_8.name()))
		        .andExpect(jsonPath("$.error").value("INTERNAL_SERVER_ERROR"))
		        .andExpect(jsonPath("$.detailsHumanReadable").value("Simple json exception message"))
		        .andExpect(jsonPath("$.stackTrace").exists());

	}

}
