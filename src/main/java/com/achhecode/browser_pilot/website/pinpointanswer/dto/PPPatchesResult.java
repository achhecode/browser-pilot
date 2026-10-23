
package com.achhecode.browser_pilot.website.pinpointanswer.dto;

import java.util.List;

import com.achhecode.browser_pilot.grid.Coordinates;

public record PPPatchesResult(
        int patchSize,
        List<Coordinates> coordinates,
        Coordinates startCoordinates,
        List<List<Direction>> directions
) {
    public enum Direction {
        UP,
        DOWN,
        LEFT,
        RIGHT
    }
}
