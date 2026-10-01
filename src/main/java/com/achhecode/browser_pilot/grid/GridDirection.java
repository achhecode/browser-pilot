package com.achhecode.browser_pilot.grid;

public enum GridDirection {

    UP(-1, 0),
    DOWN(1, 0),
    LEFT(0, -1),
    RIGHT(0, 1);

    private final int rowDelta;
    private final int columnDelta;

    GridDirection(int rowDelta, int columnDelta) {
        this.rowDelta = rowDelta;
        this.columnDelta = columnDelta;
    }

    public GridPosition move(GridPosition position) {
        return new GridPosition(
                position.row() + rowDelta,
                position.column() + columnDelta
        );
    }

    public int rowDelta() {
        return rowDelta;
    }

    public int columnDelta() {
        return columnDelta;
    }
}