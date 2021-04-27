package com.crow.iot.esp32.crowOS.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class CrowOsBackendApplication {

	public static void main(String[] args) {

		SpringApplication.run(CrowOsBackendApplication.class, args);
	}

}
