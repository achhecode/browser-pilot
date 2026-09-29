package com.achhecode.browser_pilot.website.linkedin.page.games.patches;

public class PatchesCommandExecutionException extends RuntimeException {

    private final String executionId;

    public PatchesCommandExecutionException(String message, String executionId, Throwable cause) {
        super(message + " (executionId=" + executionId + ")", cause);
        this.executionId = executionId;
    }

    public String getExecutionId() {
        return executionId;
    }
}