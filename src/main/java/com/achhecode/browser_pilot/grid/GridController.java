package com.achhecode.browser_pilot.grid;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/grid")
@RequiredArgsConstructor
public class GridController {

    private final GridService gridService;

    // GET /grid/generate?rows=3&columns=3

    @GetMapping("/generate")
    public ResponseEntity<String> generateGrid(
            @Valid GridRequest request
    ) {
        return ResponseEntity.ok(
                gridService.generateGrid(request)
        );
    }
}