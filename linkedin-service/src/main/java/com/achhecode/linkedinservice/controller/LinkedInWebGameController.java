package com.achhecode.linkedinservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.achhecode.browserpilotdto.dto.MiniSudokuRequest;
import com.achhecode.browserpilotdto.dto.QueensRequest;
import com.achhecode.browserpilotdto.dto.TangoRequest;
import com.achhecode.browserpilotdto.dto.ZipRequest;
import com.achhecode.linkedinservice.service.LinkedInWebGameAutomationService;

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