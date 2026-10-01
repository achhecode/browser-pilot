package com.achhecode.browser_pilot.grid;

public final class GridPrinter {

    // private static final int CELL_WIDTH = 3;

    private GridPrinter() {
    }

    public static String empty(
            int rows,
            int columns
    ) {
        validate(rows, columns);

        StringBuilder grid = new StringBuilder();

        appendBorder(grid, columns, "┌", "┬", "┐");

        for (int row = 0; row < rows; row++) {

            grid.append("│");

            for (int column = 0; column < columns; column++) {
                grid.append("   │");
            }

            grid.append("\n");

            if (row < rows - 1) {
                appendBorder(grid, columns, "├", "┼", "┤");
            }
        }

        appendBorder(grid, columns, "└", "┴", "┘");

        return grid.toString();
    }

    private static void appendBorder(
            StringBuilder grid,
            int columns,
            String left,
            String middle,
            String right
    ) {
        grid.append(left);

        for (int column = 0; column < columns; column++) {

            grid.append("───");

            if (column < columns - 1) {
                grid.append(middle);
            }
        }

        grid.append(right).append("\n");
    }

    private static void validate(
            int rows,
            int columns
    ) {
        if (rows <= 0 || columns <= 0) {
            throw new IllegalArgumentException(
                    "Rows and columns must be greater than zero"
            );
        }
    }
}