package com.achhecode.browser_pilot.website.linkedin.page.games.sudoku;

import java.util.List;

import com.achhecode.browser_pilot.keyboard.Digit;

public interface SudokuCommandExecutor {

    void execute(Integer gridSize, List<Digit> commands, boolean switchTab, String executionId);
}