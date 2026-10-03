package com.achhecode.browser_pilot.website.linkedin.page.games.wend;

import com.achhecode.browser_pilot.grid.GridPosition;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class NearestWendRouteOptimizer
        implements WendRouteOptimizer {

    @Override
    public List<WendPath> optimize(
            GridPosition initialPosition,
            List<WendPath> patches
    ) {
        List<WendPath> remaining =
                new ArrayList<>(patches);

        List<WendPath> result =
                new ArrayList<>(patches.size());

        GridPosition current =
                initialPosition;

        while (!remaining.isEmpty()) {

            WendPath nearest =
                    findNearest(
                            current,
                            remaining
                    );

            result.add(nearest);
            remaining.remove(nearest);

            current = nearest.end();
        }

        return List.copyOf(result);
    }

    private WendPath findNearest(
            GridPosition current,
            List<WendPath> patches
    ) {
        WendPath nearest = null;
        int minimumDistance = Integer.MAX_VALUE;

        for (WendPath patch : patches) {

            int distance =
                    distance(
                            current,
                            patch.start()
                    );

            if (distance < minimumDistance) {
                minimumDistance = distance;
                nearest = patch;
            }
        }

        return nearest;
    }

    private int distance(
            GridPosition a,
            GridPosition b
    ) {
        return Math.abs(a.row() - b.row())
                + Math.abs(a.column() - b.column());
    }
}