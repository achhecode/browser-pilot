package com.achhecode.browser_pilot.website.pinpointanswer.controller;

import com.achhecode.browser_pilot.website.pinpointanswer.dto.PPMiniSudokuResult;
import com.achhecode.browser_pilot.website.pinpointanswer.dto.PPPatchesResults;
import com.achhecode.browser_pilot.website.pinpointanswer.dto.PPQueensResult;
import com.achhecode.browser_pilot.website.pinpointanswer.dto.PPTangoResults;
import com.achhecode.browser_pilot.website.pinpointanswer.dto.PPWendResults;
import com.achhecode.browser_pilot.website.pinpointanswer.dto.PPZipResult;
import com.achhecode.browser_pilot.website.pinpointanswer.service.PinpointanswerAutomationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pinpointanswer")
public class PinpointanswerController {

    private final PinpointanswerAutomationService automationService;

    public PinpointanswerController(PinpointanswerAutomationService automationService) {
        this.automationService = automationService;
    }

    @PostMapping("/zip/solve")
    public ResponseEntity<PPZipResult> solveZip() {
        PPZipResult result = automationService.retrieveZipAnswer();
        return ResponseEntity.ok(result);
    }

    @PostMapping("/mini-sudoku/solve")
    public ResponseEntity<PPMiniSudokuResult> solveMiniSudoku() {
        PPMiniSudokuResult result = automationService.retrieveMiniSudokuAnswer();
        return ResponseEntity.ok(result);
    }

    @PostMapping("/queens/solve")
    public ResponseEntity<PPQueensResult> solveQueens() {
        PPQueensResult answer = automationService.retrieveQueenAnswer();
        return ResponseEntity.ok(answer);
    }

    @PostMapping("/tango/solve")
    public ResponseEntity<PPTangoResults> solveTango() {
        PPTangoResults answer = automationService.retrieveTangoAnswer();
        return ResponseEntity.ok(answer);
    }

    @PostMapping("/wend/solve")
    public ResponseEntity<PPWendResults> solveWend() {
        PPWendResults answer = automationService.retrieveWendAnswer();
        return ResponseEntity.ok(answer);
    }

    @PostMapping("/patches/solve")
    public ResponseEntity<PPPatchesResults> solvePatches() {
        PPPatchesResults answer = automationService.retrievePatchesAnswer();
        return ResponseEntity.ok(answer);
    }
}
