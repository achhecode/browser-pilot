package com.achhecode.browser_pilot.website.linkedin.page.games.queens;

import java.util.List;

import com.achhecode.browser_pilot.grid.Coordinates;

public record QueensRequest(
        List<Coordinates> queenPositions
){
}