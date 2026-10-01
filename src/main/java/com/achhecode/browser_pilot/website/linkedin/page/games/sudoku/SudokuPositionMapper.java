package com.achhecode.browser_pilot.website.linkedin.page.games.sudoku;

import java.util.HashMap;
import java.util.Map;

import com.achhecode.browser_pilot.grid.GridPosition;

public final class SudokuPositionMapper {

    private SudokuPositionMapper() {
    }

    public static SudokuPositions from(
            String instructions
    ) {
        validate(instructions);

        int gridSize =
                (int) Math.sqrt(instructions.length());

        Map<GridPosition, Integer> values =
                new HashMap<>(instructions.length());

        for (int index = 0;
             index < instructions.length();
             index++) {

            GridPosition position =
                    new GridPosition(
                            index / gridSize,
                            index % gridSize
                    );

            int value =
                    Character.digit(
                            instructions.charAt(index),
                            10
                    );

            values.put(position, value);
        }

        return new SudokuPositions(
                gridSize,
                Map.copyOf(values)
        );
    }

    private static void validate(
            String instructions
    ) {
        if (instructions == null || instructions.isBlank()) {
            throw new IllegalArgumentException(
                    "Sudoku instructions cannot be empty"
            );
        }

        int gridSize =
                (int) Math.sqrt(instructions.length());

        if (gridSize * gridSize != instructions.length()) {
            throw new IllegalArgumentException(
                    "Sudoku instructions must represent "
                            + "a square grid. length="
                            + instructions.length()
            );
        }

        for (int index = 0;
             index < instructions.length();
             index++) {

            char character =
                    instructions.charAt(index);

            if (!Character.isDigit(character)) {
                throw new IllegalArgumentException(
                        "Invalid Sudoku value at index "
                                + index
                                + ": "
                                + character
                );
            }
        }
    }
}