package com.achhecode.browser_pilot.website.linkedin.controller;

import com.achhecode.browser_pilot.website.linkedin.page.games.minisudoku.MiniSudokuRequest;
import com.achhecode.browser_pilot.website.linkedin.page.games.patches.PatchesRequest;
import com.achhecode.browser_pilot.website.linkedin.page.games.pinpoint.PinpointRequest;
import com.achhecode.browser_pilot.website.linkedin.page.games.queen.QueensRequest;
import com.achhecode.browser_pilot.website.linkedin.page.games.tango.TangoRequest;
import com.achhecode.browser_pilot.website.linkedin.page.games.wend.WendRequest;
import com.achhecode.browser_pilot.website.linkedin.page.games.zip.ZipRequest;
import com.achhecode.browser_pilot.website.linkedin.service.LinkedInGameService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/linkedin/games")
@Tag(
        name = "LinkedIn Games",
        description = "APIs for entering and starting LinkedIn games."
)
public class LinkedInGameController {

    private final LinkedInGameService linkedInGameService;

    public LinkedInGameController(
            LinkedInGameService linkedInGameService
    ) {
        this.linkedInGameService = linkedInGameService;
    }

    @PostMapping("/crossclimb")
    @Operation(
            summary = "Enter Crossclimb",
            description = "Navigates to the LinkedIn Crossclimb game."
    )
    public ResponseEntity<Void> enterCrossclimb() {
        linkedInGameService.enterCrossclimb();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/patches")
    @Operation(
            summary = "Enter Patches",
            description = """
                    Navigates to the LinkedIn Patches game and executes
                    the supplied patch solution.
                    """
    )
    public ResponseEntity<Void> enterPatches(
            @Valid @RequestBody PatchesRequest patchesRequest
    ) {
        linkedInGameService.enterPatches(patchesRequest);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/zip")
    @Operation(
            summary = "Enter Zip",
            description = """
                    Navigates to the LinkedIn Zip game and executes
                    the supplied sequence of keyboard directions.
                    """
    )
    public ResponseEntity<Void> enterZip(
            @Valid @RequestBody ZipRequest zipRequest
    ) {
        linkedInGameService.enterZip(zipRequest);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/mini-sudoku")
    @Operation(
            summary = "Enter Mini Sudoku",
            description = """
                    Navigates to the LinkedIn Mini Sudoku game and
                    executes the supplied Sudoku solution.
                    """
    )
    public ResponseEntity<Void> enterMiniSudoku(
            @Valid @RequestBody MiniSudokuRequest sudokuRequest
    ) {
        linkedInGameService.enterMiniSudoku(sudokuRequest);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/wend")
    @Operation(
            summary = "Enter Wënd",
            description = "Navigates to the LinkedIn Wënd game."
    )
    public ResponseEntity<Void> enterWend(
        @Valid @RequestBody WendRequest request
    ) {
        linkedInGameService.enterWend(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/tango")
    @Operation(
            summary = "Enter Tango",
            description = """
                    Navigates to the LinkedIn Tango game and executes
                    the supplied Tango solution.
                    """
    )
    public ResponseEntity<Void> enterTango(
            @Valid @RequestBody TangoRequest tangoRequest
    ) {
        linkedInGameService.enterTango(tangoRequest);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/queens")
    @Operation(
            summary = "Enter Queens",
            description = """
                    Navigates to the LinkedIn Queens game and executes
                    the supplied N-Queens solution.
                    """
    )
    public ResponseEntity<Void> enterQueens(
            @Valid @RequestBody QueensRequest queensRequest
    ) {
        linkedInGameService.enterQueens(queensRequest);
        return ResponseEntity.ok().build();
    }


    @PostMapping("/pinpoint")
    @Operation(
            summary = "Enter Pinpoint",
            description = "Navigates to the LinkedIn Pinpoint game."
    )
    public ResponseEntity<Void> enterPinpoint(@RequestBody @Valid PinpointRequest request) {
        linkedInGameService.enterPinpoint(request);
        return ResponseEntity.ok().build();
    }
}