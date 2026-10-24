package com.achhecode.browserpilotdto.dto;

import java.util.List;

public record QueensRequest(
        List<Coordinates> queenPositions
){
}