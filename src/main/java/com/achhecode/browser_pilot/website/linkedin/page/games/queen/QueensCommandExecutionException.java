package com.achhecode.browser_pilot.website.linkedin.page.games.queen;

public class QueensCommandExecutionException
        extends RuntimeException {

    private final String executionId;

    public QueensCommandExecutionException(
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