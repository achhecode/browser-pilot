package com.achhecode.browser_pilot.website.linkedin.page.games.sudoku;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import com.achhecode.browser_pilot.keyboard.Digit;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SudokuCommandService {

    private final SudokuCommandParser sudokuCommandParser;
    private final SudokuCommandExecutor sudokuCommandExecutor;

    public void executeCommand(
            Integer gridSize,
            String instruction,
            String executionId
    ) {

        List<Digit> commands =
                sudokuCommandParser.parse(instruction);

        sudokuCommandExecutor.execute(
                gridSize,
                commands,
                executionId
        );
    }
}