package com.achhecode.browser_pilot.website.linkedin.page.games.tango;

import java.util.Set;

import com.achhecode.browser_pilot.grid.GridPosition;

public record TangoPositions(
        int gridSize,
        Set<GridPosition> suns,
        Set<GridPosition> moons
) {
}