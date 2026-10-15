package com.achhecode.browser_pilot.website.instahyre;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "website.instahyre")
public record InstahyreProperties(
        String username,
        String password,
        String resumePath,
        Questionnaire questionnaire
) {

    public record Questionnaire(
            String answersFile
    ) {
    }
}