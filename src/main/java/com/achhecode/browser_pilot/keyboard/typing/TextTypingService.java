package com.achhecode.browser_pilot.keyboard.typing;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TextTypingService {

    private final TextTypingExecutor textTypingExecutor;

    public TextTypingService(
            TextTypingExecutor textTypingExecutor
    ) {
        this.textTypingExecutor = textTypingExecutor;
    }

    public TextTypingResponse type(
            TextTypingRequest request
    ) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "Request cannot be null"
            );
        }

        if (request.text() == null ||
                request.text().isEmpty()) {

            throw new IllegalArgumentException(
                    "Text cannot be empty"
            );
        }

        String executionId =
                UUID.randomUUID().toString();

        TextTypingMode mode =
                request.mode() == null
                        ? TextTypingMode.FAST
                        : request.mode();

        long start = System.nanoTime();

        switch (mode) {

            case FAST -> {

                textTypingExecutor.type(
                        request.text(),
                        executionId
                );
            }

            case KEY_EVENT -> {

                throw new UnsupportedOperationException(
                        "KEY_EVENT mode is not implemented yet"
                );
            }
        }

        long elapsedMs =
                (System.nanoTime() - start) / 1_000_000;

        return new TextTypingResponse(
                "COMPLETED",
                executionId,
                request.text().length(),
                elapsedMs,
                mode
        );
    }
}