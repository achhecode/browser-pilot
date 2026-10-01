package com.achhecode.browser_pilot.website.linkedin.page.games.patches;

import com.achhecode.browser_pilot.grid.GridPosition;

import java.util.List;

public interface PatchRouteOptimizer {

    List<PatchPath> optimize(
            GridPosition initialPosition,
            List<PatchPath> patches
    );
}