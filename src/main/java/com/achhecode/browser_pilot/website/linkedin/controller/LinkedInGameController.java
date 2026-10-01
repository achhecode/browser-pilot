package com.achhecode.browser_pilot.website.linkedin.controller;

import com.achhecode.browser_pilot.website.linkedin.page.games.minisudoku.MiniSudokuRequest;
import com.achhecode.browser_pilot.website.linkedin.page.games.patches.PatchesRequest;
import com.achhecode.browser_pilot.website.linkedin.page.games.queen.QueensRequest;
import com.achhecode.browser_pilot.website.linkedin.page.games.tango.TangoRequest;
import com.achhecode.browser_pilot.website.linkedin.page.games.zip.ZipRequest;
import com.achhecode.browser_pilot.website.linkedin.service.LinkedInGameService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/linkedin/games")
public class LinkedInGameController {

    private final LinkedInGameService linkedInGameService;

    public LinkedInGameController(
            LinkedInGameService linkedInGameService
    ) {
        this.linkedInGameService = linkedInGameService;
    }

    @PostMapping("/crossclimb")
    public ResponseEntity<Void> enterCrossclimb() {
        linkedInGameService.enterCrossclimb();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/patches")
    public ResponseEntity<Void> enterPatches(@Valid @RequestBody PatchesRequest patchesRequest) {
        linkedInGameService.enterPatches();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/zip")
    public ResponseEntity<Void> enterZip(@Valid @RequestBody ZipRequest zipRequest) {
        linkedInGameService.enterZip(zipRequest);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/mini-sudoku")
    public ResponseEntity<Void> enterMiniSudoku(@Valid @RequestBody MiniSudokuRequest sudokuRequest) {
        linkedInGameService.enterMiniSudoku(sudokuRequest);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/wend")
    public ResponseEntity<Void> enterWend() {
        linkedInGameService.enterWend();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/tango")
    public ResponseEntity<Void> enterTango(@Valid @RequestBody TangoRequest tangoRequest) {
        linkedInGameService.enterTango(tangoRequest);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/queens")
    public ResponseEntity<Void> enterQueens(@Valid @RequestBody QueensRequest queensRequest) {
        linkedInGameService.enterQueens(queensRequest);
        return ResponseEntity.ok().build();
    }
}