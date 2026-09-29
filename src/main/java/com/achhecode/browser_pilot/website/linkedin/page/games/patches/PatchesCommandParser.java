package com.achhecode.browser_pilot.website.linkedin.page.games.patches;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PatchesCommandParser {

    public List<PatchesCommandInput.Patch> parse(
            PatchesCommandInput input
    ) {

        if (input == null ||
                input.patches() == null ||
                input.patches().isEmpty()) {

            throw new IllegalArgumentException(
                    "Patches input cannot be null or empty"
            );
        }

        if (input.gridWidth() == null || input.gridHeight() == null
                || input.gridWidth() < 1 || input.gridHeight() < 1) {
            throw new IllegalArgumentException("gridWidth and gridHeight are required and must be >= 1");
        }

        for (PatchesCommandInput.Patch patch : input.patches()) {

            if (patch == null) {
                throw new IllegalArgumentException(
                        "Patch cannot be null"
                );
            }

            int r = patch.position().get(0), c = patch.position().get(1);
            
            if (r >= input.gridHeight() || c >= input.gridWidth()) {
                throw new IllegalArgumentException("Patch position out of grid: " + patch.position());
            }

            validatePosition(patch.position());
            validateMovement(patch.move());
        }

        return input.patches();
    }

    private void validatePosition(List<Integer> position) {

        if (position == null || position.size() != 2) {
            throw new IllegalArgumentException(
                    "Patch position must contain [row, column]"
            );
        }

        if (position.get(0) == null ||
                position.get(1) == null ||
                position.get(0) < 0 ||
                position.get(1) < 0) {

            throw new IllegalArgumentException(
                    "Invalid patch position: " + position
            );
        }
    }

    private void validateMovement(
            List<PatchesCommand> move
    ) {

        if (move == null) {
            throw new IllegalArgumentException(
                    "Patch movement cannot be null"
            );
        }

        for (PatchesCommand command : move) {

            if (command == null) {
                throw new IllegalArgumentException(
                        "Patch movement cannot contain null commands"
                );
            }

            if (command == PatchesCommand.SPACE) {
                throw new IllegalArgumentException(
                        "Patch movement cannot contain SPACE"
                );
            }
        }
    }
}