package com.achhecode.gameorchestrator.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration(proxyBeanMethods = false)
public class ServiceClientConfig {

    @Bean
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }

    @Bean("pinpointClient")
    public RestClient pinpointClient(RestClient.Builder builder,
            @Value("${services.pinpoint.base-url}") String baseUrl) {

        return builder.clone().baseUrl(baseUrl).build();
    }

    @Bean("linkedinClient")
    public RestClient linkedinClient(RestClient.Builder builder,
            @Value("${services.linkedin.base-url}") String baseUrl) {

        return builder.clone().baseUrl(baseUrl).build();
    }
}