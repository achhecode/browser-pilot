package com.achhecode.browser_pilot.keyboard;

public class KeyboardAutomationException extends RuntimeException {

    private final String executionId;

    public KeyboardAutomationException(
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