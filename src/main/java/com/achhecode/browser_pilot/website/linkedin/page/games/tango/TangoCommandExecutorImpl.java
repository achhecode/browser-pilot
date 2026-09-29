package com.achhecode.browser_pilot.website.linkedin.page.games.tango;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import com.achhecode.browser_pilot.keyboard.GridTraversal;
import com.achhecode.browser_pilot.keyboard.KeyboardService;
import com.achhecode.browser_pilot.keyboard.SnakeTraversalStrategy;

import java.util.List;

@Slf4j
@Component
public class TangoCommandExecutorImpl
        implements TangoCommandExecutor {

    private final KeyboardService keyboard;
    private final GridTraversal gridTraversal;

    public TangoCommandExecutorImpl(
            KeyboardService keyboard,
            GridTraversal gridTraversal
    ) {
        this.keyboard = keyboard;
        this.gridTraversal = gridTraversal;
    }

    @Override
    public synchronized void execute(
            List<TangoCommand> commands,
            String executionId
    ) {

        if (commands == null || commands.isEmpty()) {
            log.warn(
                    "No Tango commands to execute. executionId={}",
                    executionId
            );
            return;
        }

        long startTime = System.nanoTime();

        try {

            int gridSize =
                    (int) Math.ceil(Math.sqrt(commands.size()));

            gridTraversal.reset();
            for (int index = 0; index < commands.size(); index++) {

                TangoCommand command = commands.get(index);

                if (command == TangoCommand.S) {

                    keyboard.pressSpace();

                } else if (command == TangoCommand.M) {

                    keyboard.pressSpace();
                    keyboard.pressSpace();
                }

                if (index < commands.size() - 1) {

                    gridTraversal.move(
                            gridSize
                    );
                }
            }

            long totalMs =
                    (System.nanoTime() - startTime) / 1_000_000;

            log.info(
                    "Tango keyboard automation completed. " +
                    "executionId={}, commandCount={}, totalMs={}",
                    executionId,
                    commands.size(),
                    totalMs
            );

        } catch (Exception e) {

            log.error(
                    "Tango keyboard automation failed. " +
                    "executionId={}",
                    executionId,
                    e
            );

            throw new TangoCommandExecutionException(
                    "Tango keyboard automation failed",
                    executionId,
                    e
            );
        }
    }
}