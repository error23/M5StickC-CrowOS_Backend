package com.crow.iot.esp32.crowOS.backend.account;

import com.crow.iot.esp32.crowOS.backend.commons.architecture.AbstractEndpoint;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @author : error23
 * Created : 19/05/2020
 */
@RestController
@RequestMapping ("/account")
@RequiredArgsConstructor
public class AccountEndpoint extends AbstractEndpoint {

	private final AccountService accountService;

	private final AccountMapper mapper;

	@Operation (summary = "Get Account by its id")
	@GetMapping ("/{accountId:[0-9]+}")
	@ResponseBody
	@ResponseStatus (HttpStatus.OK)
	public AccountDto get(@PathVariable ("accountId") Long id) throws MethodArgumentNotValidException {

		Account account = this.accountService.get(id);
		return this.mapper.toDto(account);

	}

	@Operation (summary = "Creates one account")
	@PostMapping
	@ResponseBody
	@ResponseStatus (HttpStatus.CREATED)
	public AccountDto create(@RequestBody @Valid AccountDto dto) {

		Account account = this.accountService.create(dto);
		return this.mapper.toDto(account);
	}

	@Operation (summary = "Updates one account")
	@PatchMapping
	@ResponseBody
	@ResponseStatus (HttpStatus.ACCEPTED)
	public AccountDto update(@RequestBody AccountDto dto) throws MethodArgumentNotValidException {

		Account account = this.accountService.get(dto.getId());
		return this.mapper.toDto(this.accountService.update(account, dto));
	}

	@Operation (summary = "Deletes one Account")
	@DeleteMapping ("/{accountId:[0-9]+}")
	@ResponseBody
	@ResponseStatus (HttpStatus.ACCEPTED)
	public AccountDto delete(@PathVariable ("accountId") Long id) throws MethodArgumentNotValidException {

		Account account = this.accountService.get(id);
		this.accountService.delete(account);
		return this.mapper.toDto(account);

	}

}
