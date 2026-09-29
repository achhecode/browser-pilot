package com.achhecode.browser_pilot.website.linkedin.page.games.patches;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.achhecode.browser_pilot.keyboard.KeyboardService;

import java.util.List;

@Slf4j
@Component
public class PatchesCommandExecutorImpl implements PatchesCommandExecutor {

    @Value("${automation.keyboard.patches.command-delay-ms:200}")
    private long commandDelayMs;

    private final KeyboardService keyboard;

    // Cursor as matrix indices; the game always starts at (0,0)
    private int row;
    private int col;

    public PatchesCommandExecutorImpl(KeyboardService keyboard) {
        this.keyboard = keyboard;
    }

    @Override
    public synchronized void execute(List<PatchesRequest.Patch> patches,
                                     Integer gridWidth, Integer gridHeight, String executionId) {
        long start = System.nanoTime();
        int rows = gridHeight;
        int cols = gridWidth;
        row = 0;
        col = 0;

        log.info("Patch details {}", patches);

        try {

            int index = 0;
            for (PatchesRequest.Patch patch : patches) {
                int targetRow = patch.position().get(0);
                int targetCol = patch.position().get(1);

                log.info("Patch {}: travel ({},{}) -> ({},{})", index, row, col, targetRow, targetCol);
                moveCursorTo(targetRow, targetCol, rows, cols);

                log.info("Patch {}: start at ({},{})", index, row, col);
                pressSpace();                       // start drawing
                for (PatchesCommand move : patch.move()) {
                    step(move, rows, cols);
                }
                log.info("Patch {}: stop at ({},{})", index, row, col);
                pressSpace();                       // finish the rectangle
                index++;
            }

            log.info("Patches completed. executionId={}, patches={}, totalMs={}",
                    executionId, patches.size(), (System.nanoTime() - start) / 1_000_000);

        } catch (Exception e) {
            log.error("Patches failed. executionId={}", executionId, e);
            throw new PatchesCommandExecutionException("Patches keyboard automation failed", executionId, e);
        }
    }

    /** Horizontal first, then vertical, without wrap-around. */
    private void moveCursorTo(int targetRow, int targetCol, int rows, int cols) {
        while (col < targetCol) step(PatchesCommand.RIGHT, rows, cols);
        while (col > targetCol) step(PatchesCommand.LEFT, rows, cols);
        while (row < targetRow) step(PatchesCommand.DOWN, rows, cols);
        while (row > targetRow) step(PatchesCommand.UP, rows, cols);
    }

    private void step(PatchesCommand move, int rows, int cols) {
        int newRow = row;
        int newCol = col;

        switch (move) {
            case RIGHT -> newCol++;
            case LEFT  -> newCol--;
            case DOWN  -> newRow++;
            case UP    -> newRow--;
            default    -> throw new IllegalArgumentException("Unexpected move: " + move);
        }

        if (newRow < 0 || newCol < 0 || newRow >= rows || newCol >= cols) {
            throw new IllegalArgumentException("Move " + move + " leaves the " + rows + "x" + cols
                    + " grid from (" + row + "," + col + ")");
        }

        switch (move) {
            case RIGHT -> keyboard.pressRight();
            case LEFT  -> keyboard.pressLeft();
            case DOWN  -> keyboard.pressDown();
            case UP    -> keyboard.pressUp();
            default    -> { }
        }

        row = newRow;
        col = newCol;
        keyboard.addDelay(commandDelayMs);
    }

    private void pressSpace() {
        keyboard.pressSpace();
        keyboard.addDelay(commandDelayMs);
    }
}