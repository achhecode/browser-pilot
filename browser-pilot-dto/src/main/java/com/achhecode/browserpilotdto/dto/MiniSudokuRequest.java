package com.achhecode.browserpilotdto.dto;

import java.util.List;

public record MiniSudokuRequest(List<Cell> cells) {
    public record Cell(int value, Coordinates coordinates) {
    }
}
