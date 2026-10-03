package com.achhecode.browser_pilot.website.linkedin.page.games.wend;

import com.achhecode.browser_pilot.grid.GridPosition;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InputOrderWendRouteOptimizer
        implements WendRouteOptimizer {

    @Override
    public List<WendPath> optimize(
            GridPosition initialPosition,
            List<WendPath> wend
    ) {
        return List.copyOf(wend);
    }
}