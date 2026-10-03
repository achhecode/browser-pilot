package com.achhecode.browser_pilot.website.linkedin.page.games.zip;

import com.achhecode.browser_pilot.grid.GridDirection;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class ZipValidator {

    public List<GridDirection> validateAndMap(
            String instructions
    ) {
        if (instructions == null || instructions.isBlank()) {
            throw new IllegalArgumentException(
                    "Zip instructions cannot be empty"
            );
        }

        @SuppressWarnings("null")
        List<GridDirection> directions =
                Arrays.stream(instructions.split(","))
                        .map(String::trim)
                        .filter(token -> !token.isEmpty())
                        .map(this::toDirection)
                        .toList();

        if (directions.isEmpty()) {
            throw new IllegalArgumentException(
                    "Zip instructions contain no valid movements"
            );
        }

        return directions;
    }

    private GridDirection toDirection(
            String value
    ) {
        try {
            return GridDirection.valueOf(
                    value.toUpperCase()
            );
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Invalid Zip direction: " + value
            );
        }
    }
}