package com.achhecode.browser_pilot.keyboardmonitor.exception;

import java.util.UUID;

public class KeyboardSessionNotFoundException
        extends KeyboardMonitorException {

    public KeyboardSessionNotFoundException(UUID sessionId) {
        super("Keyboard session not found: " + sessionId);
    }
}