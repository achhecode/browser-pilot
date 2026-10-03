package com.achhecode.browser_pilot.website.linkedin.page.games.wend;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

import com.achhecode.browser_pilot.grid.GridDirection;

public record WendRequest(

        @Min(1)
        int gridWidth,

        @Min(1)
        int gridHeight,

        WendRouteStrategy routeStrategy,

        List<@NotNull @Size(min = 2, max = 2) List<@NotNull Integer>> blocked,

        @NotEmpty
        List<@Valid Wend> wend,

        boolean onlyKey,

        @Min(10)
        @Max(1000)
        Integer keySpeed
) {

    public record Wend(
            @NotNull
            @Size(min = 2, max = 2)
            List<@NotNull Integer> position,

            @NotEmpty
            List<GridDirection> move
    ) {
    }

    public WendRequest {
        if (keySpeed == null) {
            keySpeed = 10;
        }
        if (blocked == null) {
            blocked = List.of();
        }
    }
}