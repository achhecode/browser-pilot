package com.achhecode.browser_pilot.website.linkedin.page.games.zip;

import org.springframework.stereotype.Component;

import com.achhecode.browser_pilot.keyboard.ArrowDirection;

import java.util.Arrays;
import java.util.List;

@Component
public class ZipCommandParser {

    @SuppressWarnings("null")
    public List<ArrowDirection> parse(String input) {

        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException("Keyboard instruction cannot be empty");
        }

        return Arrays.stream(input.split(","))
                .map(String::trim)
                .map(String::toUpperCase)
                .map(this::parseCommand)
                .toList();
    }

    private ArrowDirection parseCommand(String command) {
        try {
            return ArrowDirection.valueOf(command);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Unsupported keyboard command: " + command
            );
        }
    }
}