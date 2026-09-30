package com.achhecode.browser_pilot.website.linkedin.page.games.zip;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.achhecode.browser_pilot.keyboard.ArrowDirection;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class ZipCommandService {

    private final ZipCommandParser parser;
    private final ZipCommandExecutor zipCommandExecutor;

    public ZipCommandService(
            ZipCommandParser parser,
            ZipCommandExecutor zipCommandExecutor
    ) {
        this.parser = parser;
        this.zipCommandExecutor = zipCommandExecutor;
    }

    /**
     * Parse and execute a keyboard instruction.
     *
     * Example:
     *
     * UP,UP,LEFT,RIGHT
     */
    public void executeCommand(
            ZipRequest zipRequest
    ) {

        String executionId = UUID.randomUUID().toString();

        long startTime = System.currentTimeMillis();
        String instruction = zipRequest.instructions();

        log.info(
                "Keyboard automation request received. executionId={}, instructionLength={}",
                executionId,
                instruction != null ? instruction.length() : 0
        );

        try {

            /*
             * Basic validation.
             */
            if (instruction == null || instruction.isBlank()) {

                log.warn(
                        "Empty keyboard instruction. executionId={}",
                        executionId
                );

                throw new IllegalArgumentException(
                        "Keyboard instruction cannot be empty"
                );
            }

            /*
             * Parse instruction.
             */
            List<ArrowDirection> commands =
                    parser.parse(instruction);

            if (commands == null || commands.isEmpty()) {

                log.warn(
                        "No commands generated from instruction. executionId={}",
                        executionId
                );

                throw new IllegalArgumentException(
                        "No valid keyboard commands found"
                );
            }

            log.info(
                    "Keyboard instruction parsed. executionId={}, commandCount={}",
                    executionId,
                    commands.size()
            );

            /*
             * Execute commands.
             */

            boolean switchTab = zipRequest.onlyKey();
            zipCommandExecutor.execute(
                    commands,
                    switchTab,
                    executionId
            );

            long duration =
                    System.currentTimeMillis() - startTime;

            log.info(
                    "Keyboard automation completed successfully. executionId={}, commandCount={}, durationMs={}",
                    executionId,
                    commands.size(),
                    duration
            );

        } catch (Exception e) {

            long duration =
                    System.currentTimeMillis() - startTime;

            log.error(
                    "Keyboard automation failed. executionId={}, durationMs={}, error={}",
                    executionId,
                    duration,
                    e.getMessage(),
                    e
            );

            throw e;
        }
    }

    /**
     * Reverse a keyboard movement instruction.
     *
     * Example:
     *
     * UP,LEFT,RIGHT
     *
     * becomes:
     *
     * LEFT,RIGHT,DOWN
     */
    public String reverseInstruction(
            String instruction
    ) {

        if (instruction == null || instruction.isBlank()) {
            return "";
        }

        String[] commands =
                instruction.split(",");

        StringBuilder result =
                new StringBuilder();

        for (int i = commands.length - 1; i >= 0; i--) {

            String command =
                    commands[i]
                            .trim()
                            .toUpperCase();

            String reversedCommand =
                    switch (command) {

                        case "UP" -> "DOWN";

                        case "DOWN" -> "UP";

                        case "LEFT" -> "RIGHT";

                        case "RIGHT" -> "LEFT";

                        default -> throw new IllegalArgumentException(
                                "Unsupported keyboard command: "
                                        + command
                        );
                    };

            if (result.length() > 0) {
                result.append(",");
            }

            result.append(reversedCommand);
        }

        return result.toString();
    }
}