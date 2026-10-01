package com.achhecode.browser_pilot.website.linkedin.page.games.zip;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record ZipRequest(

        @NotBlank(message = "Zip instructions cannot be empty")
        String instructions,

        boolean onlyKey,

        @Min(value = 1, message = "keySpeed must be at least 1")
        @Max(value = 1000, message = "keySpeed must not exceed 1000")
        Integer keySpeed

) {

    public ZipRequest {
        if (keySpeed == null) {
            keySpeed = 10;
        }
    }
}