package com.achhecode.browser_pilot.website.linkedin.page.games.patches;

import com.achhecode.browser_pilot.grid.GridPosition;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class OptimalPatchRouteOptimizer
        implements PatchRouteOptimizer {

    @Override
    public List<PatchPath> optimize(
            GridPosition initialPosition,
            List<PatchPath> patches
    ) {
        if (patches.size() <= 1) {
            return List.copyOf(patches);
        }

        SearchResult result =
                search(
                        initialPosition,
                        patches,
                        0,
                        new boolean[patches.size()],
                        new ArrayList<>(),
                        0,
                        null
                );

        return List.copyOf(result.path());
    }

    private SearchResult search(
            GridPosition current,
            List<PatchPath> patches,
            int depth,
            boolean[] used,
            List<PatchPath> path,
            int cost,
            SearchResult best
    ) {
        if (depth == patches.size()) {

            if (best == null || cost < best.cost()) {
                return new SearchResult(
                        cost,
                        new ArrayList<>(path)
                );
            }

            return best;
        }

        if (best != null && cost >= best.cost()) {
            return best;
        }

        SearchResult currentBest = best;

        for (int index = 0;
             index < patches.size();
             index++) {

            if (used[index]) {
                continue;
            }

            PatchPath patch =
                    patches.get(index);

            int travelCost =
                    distance(
                            current,
                            patch.start()
                    );

            used[index] = true;
            path.add(patch);

            currentBest =
                    search(
                            patch.end(),
                            patches,
                            depth + 1,
                            used,
                            path,
                            cost + travelCost,
                            currentBest
                    );

            path.remove(path.size() - 1);
            used[index] = false;
        }

        return currentBest;
    }

    private int distance(
            GridPosition a,
            GridPosition b
    ) {
        return Math.abs(a.row() - b.row())
                + Math.abs(a.column() - b.column());
    }

    private record SearchResult(
            int cost,
            List<PatchPath> path
    ) {
    }
}