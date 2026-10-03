package com.achhecode.browser_pilot.website.linkedin.page.games.pinpoint;

import java.util.List;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record PinpointRequest (
    @NotEmpty
    List<String> clues,
    
    @NotBlank
    @Size(min = 1, max = 100)
    String answer,

    boolean onlyKey,

    @Min(10)
    @Max(1000)
    Integer keySpeed
) {
    public PinpointRequest {
        if (keySpeed == null) {
        keySpeed = 10;
        }
    }
}