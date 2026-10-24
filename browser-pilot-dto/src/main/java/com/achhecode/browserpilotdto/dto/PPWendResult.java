package com.achhecode.browserpilotdto.dto;

import java.util.List;

public record PPWendResult(String word, List<Coordinates> coordinates, Coordinates startCoordinates,
        List<Direction> directions) {
}