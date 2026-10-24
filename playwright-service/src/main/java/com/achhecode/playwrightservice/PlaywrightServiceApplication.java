package com.achhecode.playwrightservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.achhecode.playwrightservice.config.BrowserPilotProperties;

@SpringBootApplication
@EnableConfigurationProperties ({
	BrowserPilotProperties.class
})
public class PlaywrightServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PlaywrightServiceApplication.class, args);
	}

}
