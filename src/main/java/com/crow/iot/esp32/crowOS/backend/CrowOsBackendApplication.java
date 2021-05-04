package com.crow.iot.esp32.crowOS.backend;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableCaching
public class CrowOsBackendApplication {

	@Value ("${backend.version}")
	private String backendVersion;

	public static void main(String[] args) {

		SpringApplication.run(CrowOsBackendApplication.class, args);
	}

	@Bean
	public OpenAPI customOpenAPI() {

		return new OpenAPI()
			.components(new Components())
			.schemaRequirement("httpBasic", new SecurityScheme()
				                   .scheme("basic")
				                   .type(SecurityScheme.Type.HTTP)
				                   .in(SecurityScheme.In.HEADER)
			                  )
			.info(new Info()
				      .title("Backend Template api")
				      .description("A technical documentation for base backend project")
				      .version(this.backendVersion)
				      .contact(new Contact()
					               .name("error23")
					               .email("error23.d@gmail.com")
					               .url("https://github.com/error23/")
				              )
			     );

	}
}
