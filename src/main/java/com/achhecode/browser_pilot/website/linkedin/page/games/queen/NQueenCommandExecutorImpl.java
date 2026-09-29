package com.achhecode.browser_pilot.website.linkedin.page.games.queen;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import com.achhecode.browser_pilot.keyboard.GridTraversal;
import com.achhecode.browser_pilot.keyboard.KeyboardService;
import com.achhecode.browser_pilot.keyboard.SnakeTraversalStrategy;

import java.util.List;

@Slf4j
@Component
public class NQueenCommandExecutorImpl
        implements NQueenCommandExecutor {

    private final KeyboardService keyboard;
    private final GridTraversal gridTraversal;

    public NQueenCommandExecutorImpl(
            KeyboardService keyboard,
            GridTraversal gridTraversal
    ) {
        this.keyboard = keyboard;
        this.gridTraversal = gridTraversal;
    }

    @Override
    public synchronized void execute(
            List<Integer> positions,
            String executionId
    ) {

        if (positions == null || positions.isEmpty()) {
            log.warn(
                    "No N-Queen positions to execute. executionId={}",
                    executionId
            );
            return;
        }

        long startTime = System.nanoTime();

        try {

            int n = positions.size();

            gridTraversal.reset();
            for (int index = 0; index < n * n; index++) {

                int row = index / n;
                int column = index % n;

                /*
                 * positions[row] contains
                 * the queen's column.
                 */
                if (positions.get(row) == column) {

                    keyboard.pressSpace();
                    keyboard.pressSpace();
                }

                /*
                 * Move to the next grid cell.
                 */
                if (index < n * n - 1) {

                    gridTraversal.move(
                            n
                    );
                }
            }

            long totalMs =
                    (System.nanoTime() - startTime) / 1_000_000;

            log.info(
                    "N-Queen keyboard automation completed. " +
                    "executionId={}, n={}, totalMs={}",
                    executionId,
                    n,
                    totalMs
            );

        } catch (Exception e) {

            log.error(
                    "N-Queen keyboard automation failed. " +
                    "executionId={}",
                    executionId,
                    e
            );

            throw new NQueenCommandExecutionException(
                    "N-Queen keyboard automation failed",
                    executionId,
                    e
            );
        }
    }
}