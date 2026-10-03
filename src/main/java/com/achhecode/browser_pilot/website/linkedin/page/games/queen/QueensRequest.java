package com.achhecode.browser_pilot.website.linkedin.page.games.queen;

import java.util.List;

import com.achhecode.browser_pilot.website.linkedin.page.games.LinkedInGameRequest;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record QueensRequest(
        List<Integer> positions,
        boolean onlyKey,

        @Min(10)
        @Max(1000)
        Integer keySpeed
) implements LinkedInGameRequest {
        public QueensRequest {
                if (keySpeed == null) {
                keySpeed = 10;
                }
        }
}