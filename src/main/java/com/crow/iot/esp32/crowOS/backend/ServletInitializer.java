package com.crow.iot.esp32.crowOS.backend;

import org.jetbrains.annotations.NotNull;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

public class ServletInitializer extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(@NotNull SpringApplicationBuilder application) {

		return application.sources(CrowOsBackendApplication.class);
	}

}
