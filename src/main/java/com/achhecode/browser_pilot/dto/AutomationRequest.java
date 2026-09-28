package com.achhecode.browser_pilot.dto;

import jakarta.validation.constraints.NotBlank;

public record AutomationRequest(

        @NotBlank
        String url

) {
}