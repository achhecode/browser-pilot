package com.achhecode.instahyreservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

import com.achhecode.playwrightcore.config.PlaywrightCoreConfiguration;

@SpringBootApplication
@EnableConfigurationProperties(InstahyreProperties.class)
@Import(PlaywrightCoreConfiguration.class)
public class InstahyreServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(InstahyreServiceApplication.class, args);
	}

}
