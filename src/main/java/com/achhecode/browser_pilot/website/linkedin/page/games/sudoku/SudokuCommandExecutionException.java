package com.achhecode.browser_pilot.website.linkedin.page.games.sudoku;

public class SudokuCommandExecutionException extends RuntimeException {

    private final String executionId;

    public SudokuCommandExecutionException(
            String message,
            String executionId,
            Throwable cause
    ) {
        super(message, cause);
        this.executionId = executionId;
    }

    public String getExecutionId() {
        return executionId;
    }
}