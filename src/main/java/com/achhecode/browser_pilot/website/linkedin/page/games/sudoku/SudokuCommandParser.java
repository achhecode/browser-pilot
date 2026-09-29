package com.achhecode.browser_pilot.website.linkedin.page.games.sudoku;

import org.springframework.stereotype.Component;

import com.achhecode.browser_pilot.keyboard.Digit;

import java.util.ArrayList;
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

        int length = normalized.length();
        int gridSize = (int) Math.sqrt(length);

        if (gridSize * gridSize != length) {
            throw new IllegalArgumentException(
                    "Sudoku instruction length (" + length
                            + ") must be a perfect square"
            );
        }

        List<Digit> commands = new ArrayList<>(length);

        for (char command : normalized.toCharArray()) {
            commands.add(parseCommand(command));
        }

        return toSnakeOrder(commands, gridSize);
    }

    private Digit parseCommand(char command) {

        return switch (command) {
            case '1' -> Digit.ONE;
            case '2' -> Digit.TWO;
            case '3' -> Digit.THREE;
            case '4' -> Digit.FOUR;
            case '5' -> Digit.FIVE;
            case '6' -> Digit.SIX;
            case '7' -> Digit.SEVEN;
            case '8' -> Digit.EIGHT;
            case '9' -> Digit.NINE;

            default -> throw new IllegalArgumentException(
                    "Unsupported Sudoku command: " + command
            );
        };
    }

    private List<Digit> toSnakeOrder(
            List<Digit> commands,
            int gridSize
    ) {
        List<Digit> snakeCommands =
                new ArrayList<>(commands.size());

        for (int row = 0; row < gridSize; row++) {

            int start = row * gridSize;
            int end = start + gridSize;

            if (row % 2 == 0) {
                // Left → Right
                snakeCommands.addAll(
                        commands.subList(start, end)
                );
            } else {
                // Right → Left
                for (int i = end - 1; i >= start; i--) {
                    snakeCommands.add(commands.get(i));
                }
            }
        }

        return snakeCommands;
    }
}