package com.achhecode.browser_pilot.website.linkedin.page.games.crossclimb;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

public record CrossclimbRequest(

        @NotEmpty
        List<@Valid CrossclimbClue> clues,

        boolean onlyKey,

        @Min(10)
        @Max(1000)
        Integer keySpeed
) {

    public CrossclimbRequest {
        if (keySpeed == null) {
            keySpeed = 100;
        }
    }
}