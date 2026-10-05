package com.achhecode.browser_pilot.keyboardmonitor.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record keyboardmonitorEventRequest(

        @NotBlank
        String key,

        @NotNull
        EventType type
) {

    public enum EventType {
        PRESSED,
        RELEASED
    }
}