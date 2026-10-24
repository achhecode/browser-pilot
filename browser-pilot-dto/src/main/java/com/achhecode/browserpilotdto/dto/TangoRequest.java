package com.achhecode.browserpilotdto.dto;

import java.util.List;

public record TangoRequest(
    List<Answer> answer
) {
    public record Answer(
        String value,
        Coordinates coordinates
    ) {
    }
}