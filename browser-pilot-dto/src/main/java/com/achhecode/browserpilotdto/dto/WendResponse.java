package com.achhecode.browserpilotdto.dto;

import java.util.List;

public record WendResponse(
        List<WendResult> results
) {
    public record WendResult(
            String word,
            List<Coordinates> coordinates,
            Coordinates startCoordinates,
            List<Direction> directions
    ) {
    }
}