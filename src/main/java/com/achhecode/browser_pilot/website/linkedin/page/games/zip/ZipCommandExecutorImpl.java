package com.achhecode.browser_pilot.website.linkedin.page.games.zip;

import com.achhecode.browser_pilot.grid.GridDirection;
import com.achhecode.browser_pilot.keyboard.KeyboardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class ZipCommandExecutorImpl
        implements ZipCommandExecutor {

    private final KeyboardService keyboard;
    private final ZipValidator validator;

    public ZipCommandExecutorImpl(
            KeyboardService keyboard,
            ZipValidator validator
    ) {
        this.keyboard = keyboard;
        this.validator = validator;
    }

    @Override
    public synchronized void execute(
            ZipRequest request,
            String executionId
    ) {
        long startTime = System.nanoTime();

        try {
            List<GridDirection> directions =
                    validator.validateAndMap(
                            request.instructions()
                    );

            prepare(request);

            executeDirections(
                    directions,
                    request.keySpeed()
            );

            long totalMs =
                    (System.nanoTime() - startTime) / 1_000_000;

            log.info(
                    "Zip completed. executionId={}, " +
                    "commands={}, keySpeed={}, totalMs={}",
                    executionId,
                    directions.size(),
                    request.keySpeed(),
                    totalMs
            );

        } catch (Exception e) {

            log.error(
                    "Zip execution failed. executionId={}",
                    executionId,
                    e
            );

            throw new ZipCommandExecutionException(
                    "Zip keyboard automation failed",
                    executionId,
                    e
            );
        }
    }

    private void prepare(
            ZipRequest request
    ) {
        if (request.onlyKey()) {
            keyboard.switchTab();
        }else{
            keyboard.addDelay(800);
        }
    }

    private void executeDirections(
            List<GridDirection> directions,
            int keySpeed
    ) {
        for (GridDirection direction : directions) {

            press(direction);

            keyboard.addDelay(keySpeed);
        }
    }

    private void press(
            GridDirection direction
    ) {
        switch (direction) {
            case UP -> keyboard.pressUp();
            case DOWN -> keyboard.pressDown();
            case LEFT -> keyboard.pressLeft();
            case RIGHT -> keyboard.pressRight();
        }
    }
}