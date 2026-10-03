package com.achhecode.browser_pilot.website.linkedin.page.games.queen;

import java.util.UUID;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Slf4j 
@Service
public class QueensGameService {

    private final QueensCommandService queensCommandService;


    public QueensGameService(
            QueensCommandService queensCommandService
    ) {
        this.queensCommandService = queensCommandService;
    }



    public void solve(
            QueensPage queensPage,
            QueensRequest queensRequest
    ) {
        queensPage.waitUntilReady();

        // queensPage.getBoardState();

        
        int totalCells = queensPage.totalCellIndex();

        int gridSize = queensRequest.positions().size();

        if (Math.pow(gridSize, 2) != totalCells) {
            throw new IllegalArgumentException(
                    "Positions length (" + gridSize
                            + ") does not match Sudoku cells (" + totalCells + ")"
            );
        }
        String executionId =
                UUID.randomUUID().toString();


        queensCommandService.executeCommand(queensRequest, executionId);


    }


    public void solve(
            QueensRequest queensRequest
    ) {
        

        String executionId =
                UUID.randomUUID().toString();


        queensCommandService.executeCommand(queensRequest, executionId);

    }
}