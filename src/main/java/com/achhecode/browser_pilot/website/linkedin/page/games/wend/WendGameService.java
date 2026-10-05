package com.achhecode.browser_pilot.website.linkedin.page.games.wend;

import java.util.UUID;
import org.springframework.stereotype.Service;

import com.achhecode.browser_pilot.website.linkedin.page.games.LinkedInGameSolver;

import lombok.extern.slf4j.Slf4j;

@Slf4j 
@Service 
public class WendGameService implements LinkedInGameSolver<WendRequest, WendPage>{

    private final WendCommandService commandService;
//     private final WendRequestMapper wendRequestMapper;


    public WendGameService(
            WendCommandService commandService
        //     WendRequestMapper wendRequestMapper
    ) {
        this.commandService = commandService;
        // this.wendRequestMapper = wendRequestMapper;
    }

    @Override 
    public void solve(
            WendPage wendPage,
            WendRequest wendRequest
    ) {
        wendPage.waitUntilReady();

        wendPage.getBoardState();

        // WendRequest wendRequest2 = wendRequestMapper.map(wendRequest);

        
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

    @Override 
    public void solve(
            WendRequest wendRequest
    ) {
        
        String executionId =
                UUID.randomUUID().toString();


        commandService.executeCommand(wendRequest, executionId);

    }
}