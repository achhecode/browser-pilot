package com.achhecode.browser_pilot.website.linkedin.page.games.tango;

import java.util.List;

import com.achhecode.browser_pilot.grid.Coordinates;

public record TangoRequest(
    List<Answer> answer
) {
    public record Answer(
        String value,
        Coordinates coordinates
    ) {
    }
}