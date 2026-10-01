package com.achhecode.browser_pilot.grid;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record GridRequest(
        @Min(1)
        @Max(25)
        int rows,

        @Min(1)
        @Max(25)
        int columns
) {
}