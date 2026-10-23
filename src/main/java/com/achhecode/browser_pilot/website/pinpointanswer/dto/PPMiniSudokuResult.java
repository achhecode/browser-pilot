
package com.achhecode.browser_pilot.website.pinpointanswer.dto;

import java.util.List;

import com.achhecode.browser_pilot.grid.Coordinates;

public record PPMiniSudokuResult(
        List<Cell> cells
) {
    public record Cell(
            int value,
            Coordinates coordinates
    ) {
    }
}
