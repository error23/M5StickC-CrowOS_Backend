package com.crow.iot.esp32.crowOS.backend.security.config;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author : error23
 * Created : 19/05/2020
 */
@EnableWebSecurity
public class SecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(@NotNull HttpSecurity http) throws Exception {

		http.cors()
		    .and()
		    .antMatcher("/**")
		    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
		    .and().csrf().disable()
		    .httpBasic();

	}

	@Override
	protected void configure(@NotNull AuthenticationManagerBuilder auth) {

		auth.authenticationProvider(this.daoAuthenticationProvider());
	}

	@Bean
	public DaoAuthenticationProvider daoAuthenticationProvider() {

		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(this.userDetailsService());
		authProvider.setPasswordEncoder(this.passwordEncoder());
		return authProvider;
	}

	@Override
	@Bean
	public UserDetailsService userDetailsService() {

		return new SecurityDetailsService();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {

		return new BCryptPasswordEncoder(5);
	}

}
