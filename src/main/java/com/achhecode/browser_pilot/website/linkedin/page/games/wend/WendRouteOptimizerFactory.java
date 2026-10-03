package com.achhecode.browser_pilot.website.linkedin.page.games.wend;

import java.util.Map;

import org.springframework.stereotype.Component;

@Component 
public class WendRouteOptimizerFactory {

    private final Map<WendRouteStrategy, WendRouteOptimizer> optimizers;

    public WendRouteOptimizerFactory(
            InputOrderWendRouteOptimizer inputOrder,
            NearestWendRouteOptimizer nearest,
            OptimalWendRouteOptimizer optimal
    ) {
        this.optimizers = Map.of(
                WendRouteStrategy.INPUT_ORDER, inputOrder,
                WendRouteStrategy.NEAREST, nearest,
                WendRouteStrategy.OPTIMAL, optimal
        );
    }

    public WendRouteOptimizer get(
            WendRouteStrategy strategy
    ) {
        WendRouteOptimizer optimizer =
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