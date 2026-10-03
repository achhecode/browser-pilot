package com.achhecode.browser_pilot.website.linkedin.page.games.patches;

import java.util.UUID;
import org.springframework.stereotype.Service;

import com.achhecode.browser_pilot.website.linkedin.page.games.LinkedInGameSolver;

import lombok.extern.slf4j.Slf4j;

@Slf4j 
@Service 
public class PatchesGameService implements LinkedInGameSolver<PatchesRequest, PatchesPage>{

    private final PatchesCommandService commandService;


    public PatchesGameService(
            PatchesCommandService commandService
    ) {
        this.commandService = commandService;
    }

    @Override 
    public void solve(
            PatchesPage patchesPage,
            PatchesRequest patchesRequest
    ) {
        patchesPage.waitUntilReady();

        patchesPage.getBoardState();

        
        int totalCells = patchesPage.totalCellIndex();

        int gridSize = (int) Math.sqrt(patchesRequest.gridWidth());

        if (patchesRequest.gridWidth() * patchesRequest.gridHeight() != totalCells) {
            throw new IllegalArgumentException(
                    "Positions length (" + gridSize
                            + ") does not match Sudoku cells (" + totalCells + ")"
            );
        }
        String executionId =
                UUID.randomUUID().toString();


        commandService.executeCommand(patchesRequest, executionId);

    }

    @Override 
    public void solve(
            PatchesRequest patchesRequest
    ) {
        
        String executionId =
                UUID.randomUUID().toString();


        commandService.executeCommand(patchesRequest, executionId);

    }
}