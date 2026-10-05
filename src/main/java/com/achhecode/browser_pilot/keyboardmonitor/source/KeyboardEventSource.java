package com.achhecode.browser_pilot.keyboardmonitor.source;

import com.achhecode.browser_pilot.keyboardmonitor.domain.KeyboardEvent;

import java.util.function.Consumer;

public interface KeyboardEventSource {

    void start(Consumer<KeyboardEvent> eventConsumer);

    void stop();

    boolean isRunning();
}