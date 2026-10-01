package com.achhecode.browser_pilot.grid;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/grid")
@RequiredArgsConstructor
@Tag(
        name = "Grid",
        description = "Grid generation and testing utilities."
)
public class GridController {

    private final GridService gridService;


    // GET /grid/generate?rows=3&columns=3

    @GetMapping("/generate")
    @Operation(
            summary = "Generate an empty grid",
            description = "Generates a text-based grid for keyboard navigation testing."
    )
    public ResponseEntity<String> generateGrid(
            @Valid GridRequest request
    ) {
        return ResponseEntity.ok(
                gridService.generateGrid(request)
        );
    }
}