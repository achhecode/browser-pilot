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

    public String expandInstruction(String instruction) {

        return instruction
                .toUpperCase()
                .chars()
                .mapToObj(c -> switch (c) {
                    case 'U' -> "UP";
                    case 'D' -> "DOWN";
                    case 'L' -> "LEFT";
                    case 'R' -> "RIGHT";
                    default -> throw new IllegalArgumentException(
                            "Invalid instruction: " + (char) c
                    );
                })
                .collect(Collectors.joining(","));
    }

    public String reverseInstruction(String instruction) {

        if (instruction == null || instruction.isBlank()) {
            return "";
        }

        String[] commands = instruction.split(",");

        List<String> reversed = Arrays.asList(commands);

        Collections.reverse(reversed);

        return String.join(",", reversed);
    }
}