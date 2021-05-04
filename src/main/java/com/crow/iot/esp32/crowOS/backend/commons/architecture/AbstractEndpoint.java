package com.crow.iot.esp32.crowOS.backend.commons.architecture;

import com.crow.iot.esp32.crowOS.backend.commons.DuplicatedResourceException;
import com.crow.iot.esp32.crowOS.backend.commons.I18nHelper;
import com.crow.iot.esp32.crowOS.backend.commons.ResourceNotFoundException;
import com.crow.iot.esp32.crowOS.backend.commons.architecture.dto.ExceptionDto;
import com.crow.iot.esp32.crowOS.backend.security.MissingPermissionException;
import com.crow.iot.esp32.crowOS.backend.security.SecurityTools;
import com.crow.iot.esp32.crowOS.backend.security.role.permission.Privilege;
import com.crow.iot.esp32.crowOS.backend.security.role.permission.SecuredResource;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.xnap.commons.i18n.I18n;

import java.util.Arrays;

/**
 * @author : error23
 * Created : 18/05/2020
 */
@RestControllerAdvice
@CrossOrigin (allowCredentials = "true")
@PreAuthorize ("isAuthenticated()")
@SecurityRequirement (name = "httpBasic")
@Slf4j
public class AbstractEndpoint {

	@ExceptionHandler ({ Throwable.class })
	@ResponseStatus (HttpStatus.INTERNAL_SERVER_ERROR)
	@ResponseBody
	@PreAuthorize ("permitAll()")
	public ExceptionDto defaultException(Throwable e) {

		log.error("Internal Server Error: ", e);

		ExceptionDto response = ExceptionDto.builder()
		                                    .error("INTERNAL_SERVER_ERROR")
		                                    .detailsHumanReadable(e.getLocalizedMessage())
		                                    .locale(LocaleContextHolder.getLocale().toString())
		                                    .build();

		if (SecurityTools.canConnectedAccount(Privilege.READ, SecuredResource.STACK_TRACE, null)) {
			response.setStackTrace(Arrays.toString(e.getStackTrace()));
		}

		return response;
	}

	@ExceptionHandler ({ ResourceNotFoundException.class, HttpClientErrorException.NotFound.class })
	@ResponseStatus (HttpStatus.NOT_FOUND)
	@ResponseBody
	@PreAuthorize ("permitAll()")
	public ExceptionDto resourceNotFound(Exception e) {

		log.debug("Resource not found:", e);

		ExceptionDto response = ExceptionDto.builder()
		                                    .error("RESOURCE_NOT_FOUND")
		                                    .detailsHumanReadable(e.getLocalizedMessage())
		                                    .locale(LocaleContextHolder.getLocale().toString())
		                                    .build();

		if (SecurityTools.canConnectedAccount(Privilege.READ, SecuredResource.STACK_TRACE, null)) {
			response.setStackTrace(Arrays.toString(e.getStackTrace()));
		}
		return response;

	}

	@ExceptionHandler ({
		HttpClientErrorException.BadRequest.class,
		MissingServletRequestParameterException.class,
		MethodArgumentNotValidException.class,
		HttpMessageNotReadableException.class,
		HttpRequestMethodNotSupportedException.class,
		DuplicatedResourceException.class
	})
	@ResponseStatus (HttpStatus.BAD_REQUEST)
	@ResponseBody
	@PreAuthorize ("permitAll()")
	public ExceptionDto badRequest(Exception e) {

		log.info("Bad request: ", e);

		ExceptionDto response = ExceptionDto.builder().error("BAD_REQUEST")
		                                    .locale(LocaleContextHolder.getLocale().toString())
		                                    .build();
		if (e instanceof MethodArgumentNotValidException) {
			I18n i18n = I18nHelper.getI18n();

			MethodArgumentNotValidException exception = (MethodArgumentNotValidException) e;

			StringBuilder message = new StringBuilder(i18n.trn("Input validation failed with {0} error: ", "Input validation failed with {0} errors: ", exception.getBindingResult().getErrorCount(), exception.getBindingResult().getErrorCount()));

			for (FieldError error : exception.getBindingResult().getFieldErrors()) {
				message.append(i18n.tr("Field {0}", error.getField()));
				message.append(" ");
				message.append(error.getDefaultMessage());
				message.append(" ; ");
			}
			response.setDetailsHumanReadable(message.toString());
		}
		else {
			response.setDetailsHumanReadable(e.getLocalizedMessage());
		}

		if (SecurityTools.canConnectedAccount(Privilege.READ, SecuredResource.STACK_TRACE, null)) {
			response.setStackTrace(Arrays.toString(e.getStackTrace()));
		}
		return response;
	}

	@ExceptionHandler ({ BadCredentialsException.class, UsernameNotFoundException.class, AccessDeniedException.class })
	@ResponseStatus (HttpStatus.UNAUTHORIZED)
	@ResponseBody
	@PreAuthorize ("permitAll()")
	public ExceptionDto badCredentials(RuntimeException e) {

		log.trace("Bad credentials: ", e);

		ExceptionDto response = ExceptionDto.builder()
		                                    .error("BAD_CREDENTIALS_EXCEPTION")
		                                    .detailsHumanReadable(e.getLocalizedMessage())
		                                    .locale(LocaleContextHolder.getLocale().toString())
		                                    .build();

		if (SecurityTools.canConnectedAccount(Privilege.READ, SecuredResource.STACK_TRACE, null)) {
			response.setStackTrace(Arrays.toString(e.getStackTrace()));
		}

		return response;
	}

	@ExceptionHandler (MissingPermissionException.class)
	@ResponseStatus (HttpStatus.FORBIDDEN)
	@ResponseBody
	@PreAuthorize ("permitAll()")
	public ExceptionDto missingPermissionException(Exception e) {

		log.trace("Missing permission: ", e);

		ExceptionDto response = ExceptionDto.builder()
		                                    .error("MISSING_PERMISSION")
		                                    .detailsHumanReadable(e.getLocalizedMessage())
		                                    .locale(LocaleContextHolder.getLocale().toString())
		                                    .build();

		if (SecurityTools.canConnectedAccount(Privilege.READ, SecuredResource.STACK_TRACE, null)) {
			response.setStackTrace(Arrays.toString(e.getStackTrace()));
		}

		return response;
	}

}
