package com.crow.iot.esp32.crowOS.backend;

import com.crow.iot.esp32.crowOS.backend.commons.json.JsonHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Locale;

/**
 * @author : error23
 * Created : 03/04/2020
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Bean
	@Primary
	public ObjectMapper customJson() {

		return JsonHelper.getParser();
	}

	@Bean
	public LocaleResolver localeResolver() {

		SessionLocaleResolver resolver = new SessionLocaleResolver();
		resolver.setDefaultLocale(Locale.ENGLISH);
		return resolver;
	}

	@Bean
	public LocaleChangeInterceptor localeChangeInterceptor() {

		LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
		localeChangeInterceptor.setParamName("lc");
		return localeChangeInterceptor;
	}

	@Override
	public void addInterceptors(@NotNull InterceptorRegistry registry) {

		registry.addInterceptor(this.localeChangeInterceptor());
	}

}
