package com.achhecode.browser_pilot.website.linkedin.page.games.wend;

import com.achhecode.browser_pilot.grid.GridDirection;
import com.achhecode.browser_pilot.grid.GridPosition;

import java.util.List;

public record WendPath(
        GridPosition start,
        List<GridDirection> moves,
        GridPosition end
) {
}