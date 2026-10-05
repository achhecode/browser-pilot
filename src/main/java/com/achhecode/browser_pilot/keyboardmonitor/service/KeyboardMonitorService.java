package com.achhecode.browser_pilot.keyboardmonitor.service;

import com.achhecode.browser_pilot.keyboardmonitor.domain.KeyboardEvent;
import com.achhecode.browser_pilot.keyboardmonitor.domain.KeyboardMonitorMode;
import com.achhecode.browser_pilot.keyboardmonitor.domain.KeyboardSession;

import java.util.UUID;

public interface KeyboardMonitorService {

    KeyboardSession start(
            KeyboardMonitorMode mode
    );

    KeyboardSession stop(
            UUID sessionId
    );

    KeyboardSession status(
            UUID sessionId
    );

    void accept(
            UUID sessionId,
            KeyboardEvent event
    );
}