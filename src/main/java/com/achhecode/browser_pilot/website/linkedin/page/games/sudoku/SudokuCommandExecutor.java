package com.achhecode.browser_pilot.website.linkedin.page.games.sudoku;

import com.achhecode.browser_pilot.website.linkedin.page.games.minisudoku.MiniSudokuRequest;

public interface SudokuCommandExecutor {

    void execute(MiniSudokuRequest miniSudokuRequest, String executionId);
}