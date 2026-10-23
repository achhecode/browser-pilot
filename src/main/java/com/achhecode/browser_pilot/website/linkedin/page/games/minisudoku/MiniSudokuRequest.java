package com.achhecode.browser_pilot.website.linkedin.page.games.minisudoku;

import java.util.List;
import com.achhecode.browser_pilot.grid.Coordinates;

public record MiniSudokuRequest(List<Cell> cells) {
    public record Cell(int value, Coordinates coordinates) {
    }
}
