package com.achhecode.browser_pilot.website.linkedin.page.games.tango;

import java.util.HashSet;
import java.util.Set;

import com.achhecode.browser_pilot.grid.GridPosition;

public final class TangoPositionMapper {

        private static final char SUN = 'S';
        private static final char MOON = 'M';

        private TangoPositionMapper() {
        }

        public static TangoPositions from(
                        String instructions) {
                validate(instructions);

                int gridSize = (int) Math.sqrt(instructions.length());

                Set<GridPosition> suns = new HashSet<>();
                Set<GridPosition> moons = new HashSet<>();

                for (int index = 0; index < instructions.length(); index++) {

                        GridPosition position = new GridPosition(
                                        index / gridSize,
                                        index % gridSize);

                        switch (instructions.charAt(index)) {

                                case SUN -> suns.add(position);

                                case MOON -> moons.add(position);

                                default -> throw new IllegalArgumentException(
                                                "Invalid Tango instruction at index "
                                                                + index);
                        }
                }

                return new TangoPositions(
                                gridSize,
                                Set.copyOf(suns),
                                Set.copyOf(moons));
        }

        private static void validate(
                        String instructions) {
                if (instructions == null || instructions.isBlank()) {
                        throw new IllegalArgumentException(
                                        "Tango instructions cannot be empty");
                }

                int gridSize = (int) Math.sqrt(instructions.length());

                if (gridSize * gridSize != instructions.length()) {
                        throw new IllegalArgumentException(
                                        "Tango instructions must represent "
                                                        + "a square grid. length="
                                                        + instructions.length());
                }
        }
}