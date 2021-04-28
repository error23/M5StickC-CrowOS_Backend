package com.crow.iot.esp32.crowOS.backend.commons.architecture;

import com.crow.iot.esp32.crowOS.backend.account.AccountDto;
import com.crow.iot.esp32.crowOS.backend.commons.ResourceNotFoundException;
import com.crow.iot.esp32.crowOS.backend.commons.json.JsonException;
import com.crow.iot.esp32.crowOS.backend.security.MissingPermissionException;
import com.crow.iot.esp32.crowOS.backend.security.role.permission.Privilege;
import com.crow.iot.esp32.crowOS.backend.security.role.permission.SecuredResource;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpClientErrorException;

import javax.validation.Valid;
import java.nio.charset.StandardCharsets;

@Controller
public class SimpleTestEndpoint extends AbstractEndpoint {

	@GetMapping ("/resourceNotFound")
	public void resourceNotFound() {

		throw new ResourceNotFoundException("test", 1L);

	}

	@GetMapping ("/resourceNotFoundNoIds")
	public void resourceNotFoundNoIds() {

		throw new ResourceNotFoundException("test");
	}

	@GetMapping ("/badRequest")
	public void badRequest() {

		throw HttpClientErrorException.BadRequest.create(HttpStatus.BAD_REQUEST, null, null, null, StandardCharsets.UTF_8);

	}

	@PostMapping ("/badRequestValidationError")
	@ResponseBody
	@ResponseStatus (HttpStatus.ACCEPTED)
	public void badRequestValidationError(@RequestBody @Valid AccountDto dto) {
		// Do nothing here

	}

	@GetMapping ("/missingParameters")
	public void missingServletRequestParameterException(@RequestParam Long id) {

		// Do nothing here
	}

	@GetMapping ("/badCredentialsNotConnected")
	public void badCredentialsNotConnected() {
		// Do nothing here

	}

	@GetMapping ("/badCredentialsWrongPassword")
	@ResponseBody
	public String badCredentialsWrongPassword() {

		return "ok";
	}

	@GetMapping ("/missingPermissionException")
	public void missingPermissionException() {

		throw new MissingPermissionException();
	}

	@GetMapping ("/missingPermissionExceptionWithMessage")
	public void missingPermissionExceptionWithMessage() {

		throw new MissingPermissionException("Account {0} doesn''t have root privilege!", 1L);
	}

	@GetMapping ("/missingPermissionExceptionWithPrivilege")
	public void missingPermissionExceptionWithPrivilege() {

		throw new MissingPermissionException(Privilege.READ, SecuredResource.ACCOUNT);
	}

	@GetMapping ("missingPermissionWithPreAuthorize")
	@PreAuthorize ("hasPermission('ACCOUNT', 'READ')")
	public void missingPermissionWithPreAuthorize() {

	}

	@GetMapping ("/jsonException")
	public void jsonException() {

		throw new JsonException("Simple json exception message");
	}

}
