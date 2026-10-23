package com.achhecode.browser_pilot.website.linkedin.controller;

import com.achhecode.browser_pilot.website.linkedin.page.games.minisudoku.MiniSudokuRequest;
import com.achhecode.browser_pilot.website.linkedin.page.games.queens.QueensRequest;
import com.achhecode.browser_pilot.website.linkedin.page.games.tango.TangoRequest;
import com.achhecode.browser_pilot.website.linkedin.page.games.zip.ZipRequest;
import com.achhecode.browser_pilot.website.linkedin.service.LinkedInWebGameAutomationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/linkedin/games/web/")
public class LinkedInWebGameController {

    private final LinkedInWebGameAutomationService linkedInWebGameAutomationService;

    public LinkedInWebGameController(LinkedInWebGameAutomationService linkedInWebGameAutomationService) {
        this.linkedInWebGameAutomationService = linkedInWebGameAutomationService;
    }

    @PostMapping("/zip")
    public ResponseEntity<Void> solveZip(@RequestBody ZipRequest request) {
        linkedInWebGameAutomationService.solveZip(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/mini-sudoku")
    public ResponseEntity<Void> solveMiniSudoku(@RequestBody MiniSudokuRequest request) {
        linkedInWebGameAutomationService.solveMiniSudoku(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/queens")
    public ResponseEntity<Void> solveQueens(@RequestBody QueensRequest request) {
        linkedInWebGameAutomationService.solveQueens(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/tango")
    public ResponseEntity<Void> solveTango(@RequestBody TangoRequest request) {
        linkedInWebGameAutomationService.solveTango(request);
        return ResponseEntity.ok().build();
    }
}