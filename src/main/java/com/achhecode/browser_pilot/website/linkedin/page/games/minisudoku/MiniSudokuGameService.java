package com.achhecode.browser_pilot.website.linkedin.page.games.minisudoku;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.achhecode.browser_pilot.website.linkedin.page.games.LinkedInGameSolver;
import com.achhecode.browser_pilot.website.linkedin.page.games.sudoku.SudokuCommandService;

import lombok.extern.slf4j.Slf4j;

@Slf4j 
@Service
public class MiniSudokuGameService implements LinkedInGameSolver<MiniSudokuRequest, MiniSudokuPage>{

    private final SudokuCommandService sudokuCommandService;


    public MiniSudokuGameService(
            SudokuCommandService sudokuCommandService
    ) {
        this.sudokuCommandService = sudokuCommandService;
    }

    @Override 
    public void solve(
            MiniSudokuPage miniSudokuPage,
            MiniSudokuRequest miniSudokuRequest
    ) {
        miniSudokuPage.waitUntilReady();

        String instructions = miniSudokuRequest.instructions();

        validateInstructions(instructions);

        int totalCells = miniSudokuPage.totalCellIndex();

        int gridSize = (int) Math.sqrt(instructions.length());

        if (instructions.length() != totalCells) {
            throw new IllegalArgumentException(
                    "Instruction length (" + instructions.length()
                            + ") does not match Sudoku cells (" + totalCells + ")"
            );
        }

        log.info("Grid size is {}", gridSize);

        // for (int i = 0; i < instructions.length(); i++) {
        //     char value = instructions.charAt(i);

        //     if (value != '0') {
        //         miniSudokuPage.setCellValue(i, value);
        //     }
        // }

        String executionId =
                UUID.randomUUID().toString();
        
        sudokuCommandService.executeCommand(miniSudokuRequest, executionId);

    }

    private void validateInstructions(String instructions) {

        if (instructions == null || instructions.isBlank()) {
            throw new IllegalArgumentException(
                    "Sudoku instructions cannot be empty"
            );
        }

        int length = instructions.length();

        if (length > 100) {
            throw new IllegalArgumentException(
                    "Sudoku cannot contain more than 100 cells"
            );
        }

        int size = (int) Math.sqrt(length);

        if (size * size != length) {
            throw new IllegalArgumentException(
                    "Sudoku instruction length must be a perfect square: 1, 4, 9, 16, 25, ..."
            );
        }

        for (char value : instructions.toCharArray()) {
            if (value != '0' && (value < '1' || value > '9')) {
                throw new IllegalArgumentException(
                        "Invalid Sudoku value: '" + value
                                + "'. Expected digits 1-9 or 0 for empty."
                );
            }
        }
    }

    @Override 
    public void solve(
            MiniSudokuRequest miniSudokuRequest
    ) {
        
        String executionId =
                UUID.randomUUID().toString();


        sudokuCommandService.executeCommand(miniSudokuRequest, executionId);

    }
}