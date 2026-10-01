package com.achhecode.browser_pilot.website.linkedin.page.games.patches;

import java.util.Map;

import org.springframework.stereotype.Component;

@Component 
public class PatchRouteOptimizerFactory {

    private final Map<PatchRouteStrategy, PatchRouteOptimizer> optimizers;

    public PatchRouteOptimizerFactory(
            InputOrderPatchRouteOptimizer inputOrder,
            NearestPatchRouteOptimizer nearest,
            OptimalPatchRouteOptimizer optimal
    ) {
        this.optimizers = Map.of(
                PatchRouteStrategy.INPUT_ORDER, inputOrder,
                PatchRouteStrategy.NEAREST, nearest,
                PatchRouteStrategy.OPTIMAL, optimal
        );
    }

    public PatchRouteOptimizer get(
            PatchRouteStrategy strategy
    ) {
        PatchRouteOptimizer optimizer =
                optimizers.get(strategy);

        if (optimizer == null) {
            throw new IllegalArgumentException(
                    "Unsupported route strategy: "
                            + strategy
            );
        }

        return optimizer;
    }
}