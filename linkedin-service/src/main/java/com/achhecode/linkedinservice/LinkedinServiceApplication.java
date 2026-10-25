package com.achhecode.linkedinservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import com.achhecode.playwright.config.PlaywrightCoreConfiguration;

@SpringBootApplication
@Import(PlaywrightCoreConfiguration.class)
public class LinkedinServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(LinkedinServiceApplication.class, args);
	}

}
