package com.achhecode.browserpilotdto.dto;

import java.util.List;

public record TangoResponse(
        List<TangoResult> results
) {
    public record TangoResult(
            String value,
            Coordinates coordinates
    ) {
    }
}