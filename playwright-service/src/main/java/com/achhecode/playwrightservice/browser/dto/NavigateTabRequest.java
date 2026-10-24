package com.achhecode.playwrightservice.browser.dto;

import jakarta.validation.constraints.NotBlank;

public record NavigateTabRequest(
        @NotBlank(message = "URL must not be blank")
        String url
) {
}
