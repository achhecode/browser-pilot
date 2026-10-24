package com.achhecode.playwrightservice.exception;

public class BrowserLifecycleException extends RuntimeException {

    public BrowserLifecycleException(String message) {
        super(message);
    }

    public BrowserLifecycleException(
            String message,
            Throwable cause
    ) {
        super(message, cause);
    }
}