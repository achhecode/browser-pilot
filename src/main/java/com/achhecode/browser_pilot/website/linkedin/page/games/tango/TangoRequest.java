package com.achhecode.browser_pilot.website.linkedin.page.games.tango;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record TangoRequest (
    String instructions,
    boolean onlyKey,
    @Min(10)
    @Max(1000)
    Integer keySpeed
) {
    public TangoRequest {
        if (keySpeed == null) {
        keySpeed = 10;
        }
    }
}