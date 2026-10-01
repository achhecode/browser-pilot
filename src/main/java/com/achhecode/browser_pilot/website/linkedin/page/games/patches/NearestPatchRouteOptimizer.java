package com.achhecode.browser_pilot.website.linkedin.page.games.patches;

import com.achhecode.browser_pilot.grid.GridPosition;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class NearestPatchRouteOptimizer
        implements PatchRouteOptimizer {

    @Override
    public List<PatchPath> optimize(
            GridPosition initialPosition,
            List<PatchPath> patches
    ) {
        List<PatchPath> remaining =
                new ArrayList<>(patches);

        List<PatchPath> result =
                new ArrayList<>(patches.size());

        GridPosition current =
                initialPosition;

        while (!remaining.isEmpty()) {

            PatchPath nearest =
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

    private PatchPath findNearest(
            GridPosition current,
            List<PatchPath> patches
    ) {
        PatchPath nearest = null;
        int minimumDistance = Integer.MAX_VALUE;

        for (PatchPath patch : patches) {

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