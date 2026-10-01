package com.achhecode.browser_pilot.grid;

import org.springframework.stereotype.Component;

import com.achhecode.browser_pilot.keyboard.KeyboardService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public final class GridTraversal {

    private final KeyboardService keyboard;

    public GridTraversal(KeyboardService keyboard) {
        this.keyboard = keyboard;
    }

    public void move(
        GridPosition current,
        GridPosition next
    ) {
        validateAdjacent(current, next);

        if (next.row() > current.row()) {
            keyboard.pressDown();
        } else if (next.column() > current.column()) {
            keyboard.pressRight();
        } else {
            keyboard.pressLeft();
        }

        log.info(
                "Moved: {} -> {}",
                current,
                next
        );
    }


    private static void validateAdjacent(
            GridPosition current,
            GridPosition next
    ) {
        int rowDelta =
                Math.abs(next.row() - current.row());

        int columnDelta =
                Math.abs(next.column() - current.column());

        if (rowDelta + columnDelta != 1) {
            throw new IllegalArgumentException(
                    "Non-adjacent grid positions: "
                            + current + " -> " + next
            );
        }
    }
}