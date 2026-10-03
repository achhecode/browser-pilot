package com.achhecode.browser_pilot.website.linkedin.page.games.tango;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.achhecode.browser_pilot.website.linkedin.page.games.LinkedInGameSolver;

import lombok.extern.slf4j.Slf4j;

@Slf4j 
@Service 
public class TangoGameService implements LinkedInGameSolver<TangoRequest, TangoPage>{

    private final TangoCommandService tangoCommandService;


    public TangoGameService(
            TangoCommandService tangoCommandService
    ) {
        this.tangoCommandService = tangoCommandService;
    }


    @Override 
    public void solve(
            TangoPage tangoPage,
            TangoRequest tangoRequest
    ) {
        tangoPage.waitUntilReady();

        tangoPage.getBoardState();

        
        int totalCells = tangoPage.totalCellIndex();

        int gridSize = (int) Math.sqrt(tangoRequest.instructions().length());

        if (Math.pow(gridSize, 2) != totalCells) {
            throw new IllegalArgumentException(
                    "Positions length (" + gridSize
                            + ") does not match Sudoku cells (" + totalCells + ")"
            );
        }
        String executionId =
                UUID.randomUUID().toString();


        tangoCommandService.executeCommand(tangoRequest, executionId);


    }


    @Override 
    public void solve(
            TangoRequest tangoRequest
    ) {
        
        String executionId =
                UUID.randomUUID().toString();


        tangoCommandService.executeCommand(tangoRequest, executionId);

    }
}