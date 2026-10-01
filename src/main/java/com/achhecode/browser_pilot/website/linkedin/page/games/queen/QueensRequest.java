package com.achhecode.browser_pilot.website.linkedin.page.games.queen;

import java.util.List;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
// import jakarta.validation.constraints.NotNull;

public record QueensRequest(
        List<Integer> positions,
        boolean onlyKey,

        // @NotNull
        @Min(10)
        @Max(1000)
        Integer keySpeed
) {
        public QueensRequest {
                if (keySpeed == null) {
                keySpeed = 10;
                }
        }
}