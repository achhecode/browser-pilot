package com.achhecode.browser_pilot.keyboardmonitor.domain;

import java.time.Instant;
import java.util.Objects;

public record KeyboardEvent(
        String key,
        KeyboardEventType type,
        Instant timestamp
) {

    public KeyboardEvent {

        Objects.requireNonNull(
                key,
                "key must not be null"
        );

        Objects.requireNonNull(
                type,
                "type must not be null"
        );

        Objects.requireNonNull(
                timestamp,
                "timestamp must not be null"
        );
    }

    public static KeyboardEvent pressed(String key) {

        return new KeyboardEvent(
                key,
                KeyboardEventType.PRESSED,
                Instant.now()
        );
    }

    public static KeyboardEvent released(String key) {

        return new KeyboardEvent(
                key,
                KeyboardEventType.RELEASED,
                Instant.now()
        );
    }
}