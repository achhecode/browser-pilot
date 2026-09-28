package com.achhecode.browser_pilot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.achhecode.browser_pilot.config.BrowserPilotProperties;

@SpringBootApplication
@EnableConfigurationProperties(BrowserPilotProperties.class)
public class BrowserPilotApplication {

	public static void main(String[] args) {
		SpringApplication.run(BrowserPilotApplication.class, args);
	}

}
