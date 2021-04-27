package com.crow.iot.esp32.crowOS.backend.account;

import com.crow.iot.esp32.crowOS.backend.commons.architecture.dto.IdDto;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Locale;

/**
 * @author : error23
 * Created : 18/05/2020
 */
@Getter
@Setter
public class AccountDto extends IdDto {

	@NotNull
	private String firstName;

	@NotNull
	private String lastName;

	@NotNull
	private String email;

	@NotNull
	private String password;

	private Locale locale;
	private Boolean enabled;
	private List<IdDto> roles;

}
