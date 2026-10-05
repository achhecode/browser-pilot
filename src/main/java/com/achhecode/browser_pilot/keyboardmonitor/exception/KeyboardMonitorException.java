package com.achhecode.browser_pilot.keyboardmonitor.exception;

public class KeyboardMonitorException extends RuntimeException {

    public KeyboardMonitorException(String message) {
        super(message);
    }

    public KeyboardMonitorException(
            String message,
            Throwable cause
    ) {
        super(message, cause);
    }
}