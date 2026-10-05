package com.achhecode.browser_pilot.keyboardmonitor.domain;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class KeyboardSession {

    private final UUID id;
    private final KeyboardMonitorMode mode;
    private final Instant startedAt;

    private final List<KeyboardEvent> events =
            new ArrayList<>();

    private Instant stoppedAt;

    public KeyboardSession(
            UUID id,
            KeyboardMonitorMode mode
    ) {
        this.id = id;
        this.mode = mode;
        this.startedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public KeyboardMonitorMode getMode() {
        return mode;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public synchronized Instant getStoppedAt() {
        return stoppedAt;
    }

    public synchronized boolean isRunning() {
        return stoppedAt == null;
    }

    public synchronized void record(
            KeyboardEvent event
    ) {

        if (!isRunning()) {
            return;
        }

        events.add(event);
    }

    public synchronized List<KeyboardEvent> getEvents() {

        return List.copyOf(events);
    }

    public synchronized void stop() {

        if (isRunning()) {
            stoppedAt = Instant.now();
        }
    }
}