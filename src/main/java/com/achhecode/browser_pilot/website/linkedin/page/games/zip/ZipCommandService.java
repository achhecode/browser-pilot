package com.achhecode.browser_pilot.website.linkedin.page.games.zip;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class ZipCommandService {

    private final ZipCommandExecutor executor;

    public ZipCommandService(
            ZipCommandExecutor executor
    ) {
        this.executor = executor;
    }

    public void execute(
            ZipRequest request
    ) {
        String executionId =
                UUID.randomUUID().toString();

        log.info(
                "Executing Zip. executionId={}, instructionLength={}",
                executionId,
                request.instructions().length()
        );

        executor.execute(
                request,
                executionId
        );
    }

    public String reverseInstruction(
            String instruction
    ) {
        if (instruction == null || instruction.isBlank()) {
            return "";
        }

        String[] commands =
                instruction.split(",");

        StringBuilder result =
                new StringBuilder(
                        instruction.length()
                );

        for (int i = commands.length - 1; i >= 0; i--) {

            if (result.length() > 0) {
                result.append(',');
            }

            result.append(
                    reverse(commands[i].trim())
            );
        }

        return result.toString();
    }

    private String reverse(
            String direction
    ) {
        return switch (direction.toUpperCase()) {
            case "UP" -> "DOWN";
            case "DOWN" -> "UP";
            case "LEFT" -> "RIGHT";
            case "RIGHT" -> "LEFT";

            default -> throw new IllegalArgumentException(
                    "Unsupported Zip direction: "
                            + direction
            );
        };
    }
}