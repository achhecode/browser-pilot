package com.achhecode.browser_pilot.website.instahyre;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "website.instahyre")
public record InstahyreProperties(
        String username,
        String password
) {
}