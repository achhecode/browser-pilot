package com.achhecode.instahyreservice;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "instahyre")
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