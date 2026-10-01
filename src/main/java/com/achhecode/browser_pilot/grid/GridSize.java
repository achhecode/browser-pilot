package com.achhecode.browser_pilot.grid;

public record GridSize(
        int rows,
        int columns
) {

    public GridSize {
        if (rows <= 0) {
            throw new IllegalArgumentException(
                    "Rows must be greater than zero"
            );
        }

        if (columns <= 0) {
            throw new IllegalArgumentException(
                    "Columns must be greater than zero"
            );
        }
    }

    public boolean contains(GridPosition position) {
        return position.row() >= 0
                && position.row() < rows
                && position.column() >= 0
                && position.column() < columns;
    }
}