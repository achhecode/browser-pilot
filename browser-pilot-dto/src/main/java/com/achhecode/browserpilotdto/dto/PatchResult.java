package com.achhecode.browserpilotdto.dto;

import java.util.List;

public record PatchResult(int patchSize, List<Coordinates> coordinates, Coordinates startCoordinates,
        List<List<Direction>> directions) {
}