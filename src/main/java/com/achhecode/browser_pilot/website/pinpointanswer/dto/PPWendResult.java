package com.achhecode.browser_pilot.website.pinpointanswer.dto;

import com.achhecode.browser_pilot.grid.Coordinates;

import java.util.List;

public record PPWendResult(
        String word,
        List<Coordinates> coordinates,
        Coordinates startCoordinates,
        List<Direction> directions
) {
    public enum Direction {
        UP,
        DOWN,
        LEFT,
        RIGHT
    }
}