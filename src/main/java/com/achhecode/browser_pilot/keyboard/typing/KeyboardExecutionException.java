package com.achhecode.browser_pilot.keyboard.typing;

public class KeyboardExecutionException extends RuntimeException {
    private final String executionId;

    public KeyboardExecutionException(String message, String executionId, Throwable cause) {
        super(message, cause);
        this.executionId = executionId;
    }

    public String getExecutionId() {
        return executionId;
    }
}