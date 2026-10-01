package com.achhecode.browser_pilot.website.linkedin.page.games.patches;

import com.achhecode.browser_pilot.grid.GridDirection;
import com.achhecode.browser_pilot.grid.GridPosition;

import java.util.List;

public record PatchPath(
        GridPosition start,
        List<GridDirection> moves,
        GridPosition end
) {
}