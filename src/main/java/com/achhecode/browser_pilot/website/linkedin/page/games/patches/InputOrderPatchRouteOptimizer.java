package com.achhecode.browser_pilot.website.linkedin.page.games.patches;

import com.achhecode.browser_pilot.grid.GridPosition;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InputOrderPatchRouteOptimizer
        implements PatchRouteOptimizer {

    @Override
    public List<PatchPath> optimize(
            GridPosition initialPosition,
            List<PatchPath> patches
    ) {
        return List.copyOf(patches);
    }
}