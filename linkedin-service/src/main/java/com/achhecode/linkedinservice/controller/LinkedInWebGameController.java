package com.achhecode.linkedinservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.achhecode.browserpilotdto.dto.MiniSudokuRequest;
import com.achhecode.browserpilotdto.dto.PatchesRequest;
import com.achhecode.browserpilotdto.dto.PinpointRequest;
import com.achhecode.browserpilotdto.dto.QueensRequest;
import com.achhecode.browserpilotdto.dto.TangoRequest;
import com.achhecode.browserpilotdto.dto.WendRequest;
import com.achhecode.browserpilotdto.dto.ZipRequest;
import com.achhecode.linkedinservice.service.LinkedInWebGameAutomationService;

@RestController
@RequestMapping("/api/linkedin-games")
public class LinkedInWebGameController {

    private final LinkedInWebGameAutomationService linkedInWebGameAutomationService;

    public LinkedInWebGameController(LinkedInWebGameAutomationService linkedInWebGameAutomationService) {
        this.linkedInWebGameAutomationService = linkedInWebGameAutomationService;
    }

    @PostMapping("/zip/solve")
    public ResponseEntity<Void> solveZip(@RequestBody ZipRequest request) {
        linkedInWebGameAutomationService.solveZip(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/mini-sudoku/solve")
    public ResponseEntity<Void> solveMiniSudoku(@RequestBody MiniSudokuRequest request) {
        linkedInWebGameAutomationService.solveMiniSudoku(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/queens/solve")
    public ResponseEntity<Void> solveQueens(@RequestBody QueensRequest request) {
        linkedInWebGameAutomationService.solveQueens(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/tango/solve")
    public ResponseEntity<Void> solveTango(@RequestBody TangoRequest request) {
        linkedInWebGameAutomationService.solveTango(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/wend/solve")
    public ResponseEntity<Void> solveWend(@RequestBody WendRequest request) {
        linkedInWebGameAutomationService.solveWend(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/patches/solve")
    public ResponseEntity<Void> solveTango(@RequestBody PatchesRequest request) {
        linkedInWebGameAutomationService.solvePatches(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/pinpoint/solve")
    public ResponseEntity<Void> solveTango(@RequestBody PinpointRequest request) {
        linkedInWebGameAutomationService.solvePinpoint(request);
        return ResponseEntity.ok().build();
    }
}