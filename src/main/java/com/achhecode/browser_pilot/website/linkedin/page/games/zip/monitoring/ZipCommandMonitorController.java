package com.achhecode.browser_pilot.website.linkedin.page.games.zip.monitoring;


import org.springframework.web.bind.annotation.*;

import com.achhecode.browser_pilot.website.linkedin.page.games.zip.ZipCommandService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/linkedin/games/zip/track")
public class ZipCommandMonitorController {

    private final ZipCommandMonitorService zipCommandMonitorService;
    private final ZipCommandService zipCommandService;

    public ZipCommandMonitorController(
            ZipCommandMonitorService zipCommandMonitorService,
            ZipCommandService zipCommandService
    ) {
        this.zipCommandMonitorService = zipCommandMonitorService;
        this.zipCommandService = zipCommandService;
    }



    // POST /api/zip/command/track/start
    @PostMapping("/start")
    public Map<String, Object> start() {

        zipCommandMonitorService.startTracking();

        return Map.of(
                "status", "TRACKING",
                "message", "Keyboard tracking started"
        );
    }

    // POST /api/zip/command/track/stop
    @PostMapping("/stop")
    public Map<String, Object> stop() {

        List<String> commands =
                zipCommandMonitorService.stopTracking();

        String instruction = String.join(",", commands);

        String reversed = zipCommandService.reverseInstruction(instruction);

        return Map.of(
                "status", "STOPPED",
                "commandCount", commands.size(),
                // "commands", commands,
                "instruction", instruction,
                "reversed", reversed
        );
    }

    // GET /api/zip/command/track
    @GetMapping
    public Map<String, Object> status() {

        List<String> commands =
                zipCommandMonitorService.getRecordedCommands();

        String instruction = String.join(",", commands);

        String reversed =
                zipCommandService.reverseInstruction(instruction);

        return Map.of(
                "tracking", zipCommandMonitorService.isTracking(),
                "commandCount", commands.size(),
                "commands", commands,
                "instruction", instruction,
                "reversed", reversed
        );
    }
}