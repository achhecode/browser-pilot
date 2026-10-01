package com.achhecode.browser_pilot.website.linkedin.page.games.sudoku;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.achhecode.browser_pilot.keyboard.Digit;
import com.achhecode.browser_pilot.keyboard.GridTraversal;
import com.achhecode.browser_pilot.keyboard.KeyboardService;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SudokuCommandExecutorImpl
        implements SudokuCommandExecutor {

    private final KeyboardService keyboardService;
    private final GridTraversal gridTraversal;

    @Value("${automation.keyboard.command-delay-ms:20}")
    private long commandDelayMs;

    @Override
    public void execute(Integer gridSize, List<Digit> commands, boolean switchTab, String executionId) {
        try {
            if(switchTab) {
                keyboardService.switchTab();
            }else{
                keyboardService.addDelay(1000); // because if solve in 1 sec then not accepted 
            }
            keyboardService.pressLeft();
            if (commandDelayMs > 0) {
                keyboardService.addDelay(commandDelayMs);
            }

            // gridTraversal.reset();
            // gridTraversal.reset();

            for (int i = 0; i < commands.size(); i++) {

                keyboardService.typeLetter(
                        commands.get(i).getCharacter()
                );

                if (commandDelayMs > 0) {
                    keyboardService.addDelay(commandDelayMs);
                }

                if (i < commands.size() - 1) {
                    // gridTraversal.move(
                    //         gridSize
                    // );
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