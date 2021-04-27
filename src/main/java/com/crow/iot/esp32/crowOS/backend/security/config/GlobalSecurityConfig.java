package com.crow.iot.esp32.crowOS.backend.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

/**
 * @author : error23
 * Created : 27/03/2020
 */
@EnableGlobalMethodSecurity (
	prePostEnabled = true,
	securedEnabled = true,
	jsr250Enabled = true
)
@Configuration
public class GlobalSecurityConfig extends GlobalMethodSecurityConfiguration {

	@Bean
	public PermissionEvaluator permissionEvaluator() {

		return new CustomPermissionEvaluator();
	}

	@Override
	protected MethodSecurityExpressionHandler createExpressionHandler() {

		DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
		expressionHandler.setPermissionEvaluator(this.permissionEvaluator());
		return expressionHandler;
	}

}


