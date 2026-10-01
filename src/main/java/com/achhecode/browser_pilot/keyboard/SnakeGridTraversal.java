package com.achhecode.browser_pilot.keyboard;

import java.util.ArrayList;
import java.util.List;

public final class SnakeGridTraversal {

    private SnakeGridTraversal() {
    }

    public static List<GridPosition> create(
            int gridSize,
            SnakeTraversalStrategy strategy
    ) {
        List<GridPosition> traversal =
                new ArrayList<>(gridSize * gridSize);

        for (int row = 0; row < gridSize; row++) {

            boolean leftToRight =
                    strategy.isLeftToRight(row);

            if (leftToRight) {
                for (int column = 0; column < gridSize; column++) {
                    traversal.add(new GridPosition(row, column));
                }
            } else {
                for (int column = gridSize - 1; column >= 0; column--) {
                    traversal.add(new GridPosition(row, column));
                }
            }
        }

        return List.copyOf(traversal);
    }
}