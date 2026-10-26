
package com.achhecode.gameorchestrator.controller;

import com.achhecode.gameorchestrator.service.GameOrchestrationService;

import tools.jackson.databind.JsonNode;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/games", produces = MediaType.APPLICATION_JSON_VALUE)
public class GameController {

    private final GameOrchestrationService orchestrationService;

    public GameController(GameOrchestrationService orchestrationService) {
        this.orchestrationService = orchestrationService;
    }

    @PostMapping("/{game}/solve")
    public JsonNode solveGame(@PathVariable String game) {
        return orchestrationService.solveAndApply(game);
    }
}
