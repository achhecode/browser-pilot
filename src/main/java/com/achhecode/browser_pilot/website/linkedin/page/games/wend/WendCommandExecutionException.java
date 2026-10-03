package com.achhecode.browser_pilot.website.linkedin.page.games.wend;

public class WendCommandExecutionException  extends RuntimeException {

    private final String executionId;

    public WendCommandExecutionException(String message, String executionId, Throwable cause) {
        super(message + " (executionId=" + executionId + ")", cause);
        this.executionId = executionId;
    }

    public String getExecutionId() {
        return executionId;
    }
}