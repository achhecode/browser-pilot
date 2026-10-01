package com.achhecode.browser_pilot.website.linkedin.page.games.queen;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.achhecode.browser_pilot.grid.GridPosition;

public final class QueenPositionMapper {

    private QueenPositionMapper() {
    }

    public static Set<GridPosition> from(
            List<Integer> positions
    ) {
        Set<GridPosition> result =
                new HashSet<>(positions.size());

        for (int row = 0; row < positions.size(); row++) {
            result.add(
                    new GridPosition(
                            row,
                            positions.get(row)
                    )
            );
        }

        return Set.copyOf(result);
    }
}