package com.achhecode.browser_pilot.website.linkedin.page.games.wend;

import com.achhecode.browser_pilot.grid.GridPosition;

import java.util.List;

public interface WendRouteOptimizer {

    List<WendPath> optimize(
            GridPosition initialPosition,
            List<WendPath> wend
    );
}