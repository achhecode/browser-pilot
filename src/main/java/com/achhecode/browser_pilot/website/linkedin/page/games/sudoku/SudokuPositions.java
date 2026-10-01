package com.achhecode.browser_pilot.website.linkedin.page.games.sudoku;

import java.util.Map;

import com.achhecode.browser_pilot.grid.GridPosition;

public record SudokuPositions(
        int gridSize,
        Map<GridPosition, Integer> values
) {
}