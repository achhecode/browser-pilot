package com.achhecode.instahyreservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(InstahyreProperties.class)
public class InstahyreServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(InstahyreServiceApplication.class, args);
	}

}
