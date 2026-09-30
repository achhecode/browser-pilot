package com.achhecode.browser_pilot.website.linkedin.page.games.queen;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class NQueenCommandService {

    private final NQueenCommandExecutor commandExecutor;

    public NQueenCommandService(
            NQueenCommandExecutor commandExecutor
    ) {
        this.commandExecutor = commandExecutor;
    }

    public int executeCommand(
            Integer gridSize,
            List<Integer> positions,
            boolean onlyKey,
            String executionId
    ) {

        long startTime = System.currentTimeMillis();

        try {

            validate(positions);

            commandExecutor.execute(
                    positions,
                    onlyKey,
                    executionId
            );

            long duration =
                    System.currentTimeMillis() - startTime;

            log.info(
                    "N-Queen automation completed. " +
                    "executionId={}, n={}, durationMs={}",
                    executionId,
                    positions.size(),
                    duration
            );

            return positions.size();

        } catch (Exception e) {

            log.error(
                    "N-Queen automation failed. executionId={}",
                    executionId,
                    e
            );

            throw e;
        }
    }

    private void validate(List<Integer> positions) {

        if (positions == null || positions.isEmpty()) {
            throw new IllegalArgumentException(
                    "N-Queen positions cannot be null or empty"
            );
        }

        int n = positions.size();

        for (int row = 0; row < n; row++) {

            Integer column = positions.get(row);

            if (column == null ||
                    column < 0 ||
                    column >= n) {

                throw new IllegalArgumentException(
                        "Invalid queen position: " +
                        "row=" + row +
                        ", column=" + column
                );
            }
        }
    }
}