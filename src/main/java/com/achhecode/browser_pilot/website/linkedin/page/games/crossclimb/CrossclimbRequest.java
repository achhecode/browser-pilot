package com.achhecode.browser_pilot.website.linkedin.page.games.crossclimb;

import java.util.List;

import com.achhecode.browser_pilot.website.linkedin.page.games.LinkedInGameRequest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Max;

public record CrossclimbRequest(

        @NotEmpty
        List<@Valid CrossclimbClue> clues,

        @NotBlank 
        String start,

        @NotBlank 
        String end,

        boolean onlyKey,

        @Min(10)
        @Max(1000)
        Integer keySpeed
) implements LinkedInGameRequest {

    public CrossclimbRequest {
        if (keySpeed == null) {
            keySpeed = 100;
        }
    }
}