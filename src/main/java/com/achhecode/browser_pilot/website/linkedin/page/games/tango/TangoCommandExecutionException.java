package com.achhecode.browser_pilot.website.linkedin.page.games.tango;

public class TangoCommandExecutionException
        extends RuntimeException {

    private final String executionId;

    public TangoCommandExecutionException(
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