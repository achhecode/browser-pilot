package com.achhecode.browser_pilot.website.linkedin.page.games.sudoku;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.achhecode.browser_pilot.keyboard.Digit;
import com.achhecode.browser_pilot.keyboard.GridTraversal;
import com.achhecode.browser_pilot.keyboard.KeyboardService;
import com.achhecode.browser_pilot.keyboard.SnakeTraversalStrategy;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SudokuCommandExecutorImpl
        implements SudokuCommandExecutor {

    private final KeyboardService keyboardService;
    private final GridTraversal sudokuTraversal;

    @Value("${automation.sudoku.grid-size:6}")
    private int gridSize;

    @Value("${automation.sudoku.traversal-strategy:LEFT_TO_RIGHT_SNAKE}")
    private SnakeTraversalStrategy traversalStrategy;

    @Value("${automation.keyboard.command-delay-ms:20}")
    private long commandDelayMs;

    @Override
    public void execute(List<Digit> commands, String executionId) {
        try {

            for (int i = 0; i < commands.size(); i++) {
                keyboardService.typeLetter(commands.get(i).getCharacter());

                if (commandDelayMs > 0) {
                    keyboardService.addDelay(commandDelayMs);
                }

                if (i < commands.size() - 1) {
                    sudokuTraversal.move(
                            i,
                            gridSize,
                            traversalStrategy
                    );
                }
            }

        } catch (Exception e) {
            log.error(
                    "Sudoku automation failed. executionId={}",
                    executionId,
                    e
            );

            throw new SudokuCommandExecutionException(
                    "Sudoku keyboard automation failed",
                    executionId,
                    e
            );
        }
    }
}