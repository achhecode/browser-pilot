package com.achhecode.browser_pilot.website.linkedin.page.games.tango;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class TangoCommandParser {

    public List<TangoCommand> parse(String instruction) {

        if (instruction == null || instruction.isBlank()) {
            throw new IllegalArgumentException(
                    "Tango instruction cannot be null or empty"
            );
        }

        String normalized = instruction
                .replaceAll("\\s+", "")
                .trim()
                .toUpperCase();

        return Arrays.stream(normalized.split(""))
                .map(this::parseCommand)
                .toList();
    }

    private TangoCommand parseCommand(String command) {
        return switch (command) {
            case "S" -> TangoCommand.S;
            case "M" -> TangoCommand.M;
            default -> throw new IllegalArgumentException(
                    "Unsupported Tango command: " + command
            );
        };
    }
}