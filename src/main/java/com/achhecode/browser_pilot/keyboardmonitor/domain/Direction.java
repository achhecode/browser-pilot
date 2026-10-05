package com.achhecode.browser_pilot.keyboardmonitor.domain;

import java.util.Arrays;
import java.util.Optional;

public enum Direction {

    UP("ArrowUp"),
    DOWN("ArrowDown"),
    LEFT("ArrowLeft"),
    RIGHT("ArrowRight");

    private final String key;

    Direction(String key) {
        this.key = key;
    }

    public String key() {
        return key;
    }

    public static Optional<Direction> fromKey(String key) {
        return Arrays.stream(values())
                .filter(direction -> direction.key.equalsIgnoreCase(key))
                .findFirst();
    }
}