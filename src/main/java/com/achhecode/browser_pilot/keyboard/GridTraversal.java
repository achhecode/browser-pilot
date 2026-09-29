package com.achhecode.browser_pilot.keyboard;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GridTraversal {

    private final KeyboardService keyboardService;

    public void move(
            int index,
            int gridSize,
            SnakeTraversalStrategy strategy
    ) {
        int row = index / gridSize;
        int column = index % gridSize;

        boolean reverse = strategy == SnakeTraversalStrategy.RIGHT_TO_LEFT_SNAKE;
        boolean leftToRight = (row % 2 == 0) != reverse;

        boolean endOfRow = leftToRight
                ? column == gridSize - 1
                : column == 0;

        if (endOfRow) {
            keyboardService.pressDown();
        } else if (leftToRight) {
            keyboardService.pressRight();
        } else {
            keyboardService.pressLeft();
        }
    }
}