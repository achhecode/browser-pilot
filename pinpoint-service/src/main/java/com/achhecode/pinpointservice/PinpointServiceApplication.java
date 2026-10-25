package com.achhecode.pinpointservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import com.achhecode.playwrightcore.config.PlaywrightCoreConfiguration;

@SpringBootApplication
@Import(PlaywrightCoreConfiguration.class)
public class PinpointServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PinpointServiceApplication.class, args);
	}

}
