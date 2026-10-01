package com.achhecode.browser_pilot.grid;

public interface GridNavigator {

    GridPosition moveTo(
            GridPosition current,
            GridPosition target
    );

    GridPosition move(
            GridPosition current,
            GridDirection direction
    );
}