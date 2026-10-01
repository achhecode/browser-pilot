package com.achhecode.browser_pilot.website.linkedin.page.games.minisudoku;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record MiniSudokuRequest (
    String instructions,
    boolean onlyKey,
    @Min (10)
    @Max(1000)
    Integer keySpeed
) {
    public MiniSudokuRequest {
        if (keySpeed == null) {
        keySpeed = 10;
        }
    }
}