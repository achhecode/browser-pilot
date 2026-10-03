package com.achhecode.browser_pilot.website.linkedin.page.games.zip;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ZipCommandService {

    private final ZipCommandExecutor executor;

    public ZipCommandService(ZipCommandExecutor executor) {
        this.executor = executor;
    }

    public void execute(ZipRequest request) {
        String executionId = UUID.randomUUID().toString();

        log.info(
                "Executing Zip. executionId={}, instructionLength={}",
                executionId,
                request.instructions().length()
        );

        executor.execute(request, executionId);
    }

    @SuppressWarnings("null")
    public String expandInstruction(String instruction) {
        if (instruction == null || instruction.isBlank()) {
            return "";
        }

        instruction = instruction.trim();

        if (instruction.contains(",")) {
            return Arrays.stream(instruction.split(","))
                    .map(String::trim)
                    .map(String::toUpperCase)
                    .peek(this::validateCommand)
                    .collect(Collectors.joining(","));
        }

        return instruction
                .toUpperCase()
                .chars()
                .mapToObj(this::expandCommand)
                .collect(Collectors.joining(","));
    }

    public String reverseInstruction(String instruction) {
        if (instruction == null || instruction.isBlank()) {
            return "";
        }

        String expanded = expandInstruction(instruction);

        List<String> commands = Arrays.asList(expanded.split(","));

        Collections.reverse(commands);

        return commands.stream()
                .map(this::reverseCommand)
                .collect(Collectors.joining(","));
    }

    private String expandCommand(int command) {
        return switch (command) {
            case 'U' -> "UP";
            case 'D' -> "DOWN";
            case 'L' -> "LEFT";
            case 'R' -> "RIGHT";
            default -> throw new IllegalArgumentException(
                    "Invalid instruction: " + (char) command
            );
        };
    }

    private String reverseCommand(String command) {
        return switch (command) {
            case "UP" -> "DOWN";
            case "DOWN" -> "UP";
            case "LEFT" -> "RIGHT";
            case "RIGHT" -> "LEFT";
            default -> throw new IllegalArgumentException(
                    "Invalid command: " + command
            );
        };
    }

    private void validateCommand(String command) {
        switch (command) {
            case "UP", "DOWN", "LEFT", "RIGHT" -> {
            }
            default -> throw new IllegalArgumentException(
                    "Invalid command: " + command
            );
        }
    }
}