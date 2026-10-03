package com.achhecode.browser_pilot.website.linkedin.page.games.wend;

import java.util.UUID;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j 
@Service 
public class WendGameService {

    private final WendCommandService commandService;


    public WendGameService(
            WendCommandService commandService
    ) {
        this.commandService = commandService;
    }


    public void solve(
            WendPage wendPage,
            WendRequest wendRequest
    ) {
        wendPage.waitUntilReady();

        wendPage.getBoardState();

        
        int totalCells = wendPage.totalCellIndex();

        int gridSize = (int) Math.sqrt(wendRequest.gridWidth());

        if (wendRequest.gridWidth() * wendRequest.gridHeight() != totalCells) {
            throw new IllegalArgumentException(
                    "Positions length (" + gridSize
                            + ") does not match Sudoku cells (" + totalCells + ")"
            );
        }
        String executionId =
                UUID.randomUUID().toString();


        commandService.executeCommand(wendRequest, executionId);

    }


    public void solve(
            WendRequest wendRequest
    ) {
        
        String executionId =
                UUID.randomUUID().toString();


        commandService.executeCommand(wendRequest, executionId);

    }
}