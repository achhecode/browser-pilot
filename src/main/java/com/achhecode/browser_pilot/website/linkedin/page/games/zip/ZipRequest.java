package com.achhecode.browser_pilot.website.linkedin.page.games.zip;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record ZipRequest(

        @Schema(
                description = "Comma-separated sequence of keyboard directions.",
                example = "DOWN,DOWN,LEFT,LEFT,UP,RIGHT"
        )
        @NotBlank(message = "Zip instructions cannot be empty")
        String instructions,

        @Schema(
                description = "Switch to the LinkedIn game tab before execution.",
                example = "false"
        )
        boolean onlyKey,

        @Schema(
                description = "Delay in milliseconds between keyboard commands.",
                example = "10",
                defaultValue = "10",
                minimum = "1",
                maximum = "1000"
        )
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