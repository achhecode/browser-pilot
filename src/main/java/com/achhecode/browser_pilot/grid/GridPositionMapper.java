package com.achhecode.browser_pilot.grid;

import java.util.ArrayList;
import java.util.List;

public final class GridPositionMapper {

    private GridPositionMapper() {
    }

    public static List<GridPosition> fromQueenPositions(
            List<Integer> positions
    ) {
        if (positions == null || positions.isEmpty()) {
            return List.of();
        }

        List<GridPosition> result = new ArrayList<>(positions.size());

        for (int row = 0; row < positions.size(); row++) {
            int column = positions.get(row);

            result.add(new GridPosition(row, column));
        }

        return result;
    }
}