
package com.achhecode.gameorchestrator.controller;

import com.achhecode.browserpilotdto.enums.LinkedInGame;
import com.achhecode.gameorchestrator.service.GameOrchestrationService;

import jakarta.validation.Valid;
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
    public JsonNode solveGame(@Valid @PathVariable LinkedInGame game) {
        return orchestrationService.solveAndApply(game);
    }
}
