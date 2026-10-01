package com.achhecode.browser_pilot.website.linkedin.page.games.sudoku;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.achhecode.browser_pilot.website.linkedin.page.games.minisudoku.MiniSudokuRequest;


@Slf4j
@Service
public class SudokuCommandService {

    private final SudokuCommandExecutor commandExecutor;

    public SudokuCommandService(
            SudokuCommandExecutor commandExecutor
    ) {
        this.commandExecutor = commandExecutor;
    }

    public int executeCommand(
        MiniSudokuRequest miniSudokuRequest,    
        String executionId
    ) {

        long startTime = System.currentTimeMillis();

        try {

            commandExecutor.execute(
                    miniSudokuRequest,
                    executionId
            );

            long duration =
                    System.currentTimeMillis() - startTime;

            log.info(
                    "N-Queen automation completed. " +
                    "executionId={}, n={}, durationMs={}",
                    executionId,
                    miniSudokuRequest.instructions().length(),
                    duration
            );

            return miniSudokuRequest.instructions().length();

        } catch (Exception e) {

            log.error(
                    "N-Queen automation failed. executionId={}",
                    executionId,
                    e
            );

            throw e;
        }
    }
}