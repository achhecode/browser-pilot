package com.achhecode.pinpointservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.achhecode.browserpilotdto.dto.MiniSudokuResponse;
import com.achhecode.browserpilotdto.dto.PatchesResponse;
import com.achhecode.browserpilotdto.dto.PinpointResponse;
import com.achhecode.browserpilotdto.dto.QueensResponse;
import com.achhecode.browserpilotdto.dto.TangoResponse;
import com.achhecode.browserpilotdto.dto.WendResponse;
import com.achhecode.browserpilotdto.dto.ZipResponse;
import com.achhecode.pinpointservice.service.PinpointanswerAutomationService;

@RestController
@RequestMapping("/api/pinpointanswer")
public class PinpointanswerController {

    private final PinpointanswerAutomationService automationService;

    public PinpointanswerController(PinpointanswerAutomationService automationService) {
        this.automationService = automationService;
    }

    @PostMapping("/zip/solve")
    public ResponseEntity<ZipResponse> solveZip() {
        ZipResponse result = automationService.retrieveZipAnswer();
        return ResponseEntity.ok(result);
    }

    @PostMapping("/mini-sudoku/solve")
    public ResponseEntity<MiniSudokuResponse> solveMiniSudoku() {
        MiniSudokuResponse result = automationService.retrieveMiniSudokuAnswer();
        return ResponseEntity.ok(result);
    }

    @PostMapping("/queens/solve")
    public ResponseEntity<QueensResponse> solveQueens() {
        QueensResponse answer = automationService.retrieveQueenAnswer();
        return ResponseEntity.ok(answer);
    }

    @PostMapping("/tango/solve")
    public ResponseEntity<TangoResponse> solveTango() {
        TangoResponse answer = automationService.retrieveTangoAnswer();
        return ResponseEntity.ok(answer);
    }

    @PostMapping("/wend/solve")
    public ResponseEntity<WendResponse> solveWend() {
        WendResponse answer = automationService.retrieveWendAnswer();
        return ResponseEntity.ok(answer);
    }

    @PostMapping("/patches/solve")
    public ResponseEntity<PatchesResponse> solvePatches() {
        PatchesResponse answer = automationService.retrievePatchesAnswer();
        return ResponseEntity.ok(answer);
    }

    @PostMapping("/pinpoint/solve")
    public ResponseEntity<PinpointResponse> solvePinpoint() {
        PinpointResponse answer = automationService.retrievePinpointAnswer();
        return ResponseEntity.ok(answer);
    }
}
