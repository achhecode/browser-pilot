package com.achhecode.browser_pilot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
// import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.achhecode.browser_pilot.config.BrowserPilotProperties;
import com.achhecode.browser_pilot.website.instahyre.InstahyreProperties;

@SpringBootApplication
// @ConfigurationPropertiesScan Global
@EnableConfigurationProperties({
    BrowserPilotProperties.class,
    InstahyreProperties.class
})
public class BrowserPilotApplication {

	public static void main(String[] args) {
		SpringApplication.run(BrowserPilotApplication.class, args);
	}
}
