package com.achhecode.browser_pilot.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "BrowserPilot API",
                version = "1.0",
                description = "API for browser automation and LinkedIn game automation."
        )
)
public class OpenApiConfig {
}