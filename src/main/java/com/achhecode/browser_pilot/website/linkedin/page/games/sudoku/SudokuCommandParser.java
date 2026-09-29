package com.achhecode.browser_pilot.website.linkedin.page.games.sudoku;

import org.springframework.stereotype.Component;

import com.achhecode.browser_pilot.keyboard.Digit;

import java.util.Arrays;
import java.util.List;

@Component
public class SudokuCommandParser {

    public List<Digit> parse(String instruction) {

        if (instruction == null || instruction.isBlank()) {
            throw new IllegalArgumentException(
                    "Sudoku instruction cannot be null or empty"
            );
        }

        String normalized = instruction.trim();

        return Arrays.stream(normalized.split(""))
                .map(this::parseCommand)
                .toList();
    }

    private Digit parseCommand(String command) {

        return switch (command) {
            case "1" -> Digit.ONE;
            case "2" -> Digit.TWO;
            case "3" -> Digit.THREE;
            case "4" -> Digit.FOUR;
            case "5" -> Digit.FIVE;
            case "6" -> Digit.SIX;
            case "7" -> Digit.SEVEN;
            case "8" -> Digit.EIGHT;
            case "9" -> Digit.NINE;

            default -> throw new IllegalArgumentException(
                    "Unsupported Sudoku command: " + command
            );
        };
    }
}