package com.achhecode.browser_pilot.website.linkedin.page.games.patches;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

import com.achhecode.browser_pilot.grid.GridDirection;
import com.achhecode.browser_pilot.website.linkedin.page.games.LinkedInGameRequest;

public record PatchesRequest(

        @Min(1)
        int gridWidth,

        @Min(1)
        int gridHeight,

        PatchRouteStrategy routeStrategy,

        @NotEmpty
        List<@Valid Patch> patches,

        boolean onlyKey,
        @Min(10)
        @Max(1000)
        Integer keySpeed
) implements LinkedInGameRequest {

    public record Patch(
            @NotNull
            @Size(min = 2, max = 2)
            List<@NotNull Integer> position,

            @NotEmpty
            List<GridDirection> move
    ) {
    }

    public PatchesRequest {
        if (keySpeed == null) {
        keySpeed = 10;
        }
    }
}