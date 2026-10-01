package com.achhecode.browser_pilot.grid;

import com.achhecode.browser_pilot.keyboard.KeyboardService;
import org.springframework.stereotype.Component;

@Component
public class KeyboardGridNavigator implements GridNavigator {

    private final KeyboardService keyboard;

    public KeyboardGridNavigator(
            KeyboardService keyboard
    ) {
        this.keyboard = keyboard;
    }

    @Override
    public GridPosition moveTo(
            GridPosition current,
            GridPosition target
    ) {
        GridPosition position = current;

        while (position.column() < target.column()) {
            position = move(position, GridDirection.RIGHT);
        }

        while (position.column() > target.column()) {
            position = move(position, GridDirection.LEFT);
        }

        while (position.row() < target.row()) {
            position = move(position, GridDirection.DOWN);
        }

        while (position.row() > target.row()) {
            position = move(position, GridDirection.UP);
        }

        return position;
    }

    @Override
    public GridPosition move(
            GridPosition current,
            GridDirection direction
    ) {
        GridPosition next =
                direction.move(current);

        press(direction);

        return next;
    }

    private void press(GridDirection direction) {
        switch (direction) {
            case UP -> keyboard.pressUp();
            case DOWN -> keyboard.pressDown();
            case LEFT -> keyboard.pressLeft();
            case RIGHT -> keyboard.pressRight();
        }
    }
}