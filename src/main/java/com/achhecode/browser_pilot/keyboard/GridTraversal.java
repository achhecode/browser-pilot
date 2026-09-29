package com.achhecode.browser_pilot.keyboard;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class GridTraversal {

    private final KeyboardService keyboardService;

    private int row;
    private int column;

    public void reset() {
        row = 0;
        column = 0;
    }

    public void move(
            int gridSize
    ) {
        boolean leftToRight = (row % 2 == 0);

        if (leftToRight) {

            if (column < gridSize - 1) {
                keyboardService.pressRight();
                column++;
            } else {
                keyboardService.pressDown();
                row++;
            }

        } else {

            if (column > 0) {
                keyboardService.pressLeft();
                column--;
            } else {
                keyboardService.pressDown();
                row++;
            }
        }

        log.info(
                "Cursor now at [{},{}]",
                row,
                column
        );
    }
}