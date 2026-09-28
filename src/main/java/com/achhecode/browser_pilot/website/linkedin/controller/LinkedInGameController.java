package com.achhecode.browser_pilot.website.linkedin.controller;

import com.achhecode.browser_pilot.website.linkedin.service.LinkedInGameService;
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
    public ResponseEntity<Void> enterPatches() {
        linkedInGameService.enterPatches();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/zip")
    public ResponseEntity<Void> enterZip() {
        linkedInGameService.enterZip();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/mini-sudoku")
    public ResponseEntity<Void> enterMiniSudoku() {
        linkedInGameService.enterMiniSudoku();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/wend")
    public ResponseEntity<Void> enterWend() {
        linkedInGameService.enterWend();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/tango")
    public ResponseEntity<Void> enterTango() {
        linkedInGameService.enterTango();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/queens")
    public ResponseEntity<Void> enterQueens() {
        linkedInGameService.enterQueens();
        return ResponseEntity.ok().build();
    }
}