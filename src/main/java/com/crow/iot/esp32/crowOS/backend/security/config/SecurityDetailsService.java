package com.crow.iot.esp32.crowOS.backend.security.config;

import com.crow.iot.esp32.crowOS.backend.account.Account;
import com.crow.iot.esp32.crowOS.backend.account.AccountService;
import org.jetbrains.annotations.NotNull;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * @author : error23
 * Created : 08/04/2020
 */
@Service
public class SecurityDetailsService implements UserDetailsService {

	private AccountService accountService;

	@Override
	public UserDetails loadUserByUsername(@NotNull String username) throws UsernameNotFoundException {

		Account account = this.accountService.get(username);
		if (account == null) throw new UsernameNotFoundException("Account not found : " + username);

		MDC.put("accountId", account.getId().toString());
		MDC.put("accountEmail", username);
		return new SecurityDetails(account);
	}

	@Autowired
	public void setAccountService(AccountService accountService) {

		this.accountService = accountService;
	}
}
