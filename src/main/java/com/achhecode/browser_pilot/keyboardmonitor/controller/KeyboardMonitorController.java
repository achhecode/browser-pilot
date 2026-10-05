package com.achhecode.browser_pilot.keyboardmonitor.controller;

import com.achhecode.browser_pilot.keyboardmonitor.domain.KeyboardMonitorMode;
import com.achhecode.browser_pilot.keyboardmonitor.domain.KeyboardSession;
import com.achhecode.browser_pilot.keyboardmonitor.service.KeyboardMonitorService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/keyboard")
public class KeyboardMonitorController {

    private final KeyboardMonitorService monitorService;

    public KeyboardMonitorController(
            KeyboardMonitorService monitorService
    ) {
        this.monitorService = monitorService;
    }

    @PostMapping("/direction/start")
    public KeyboardSession startDirectionMonitoring() {

        return monitorService.start(
                KeyboardMonitorMode.DIRECTION
        );
    }

    @PostMapping("/number/start")
    public KeyboardSession startNumberMonitoring() {

        return monitorService.start(
                KeyboardMonitorMode.NUMBER
        );
    }

    @PostMapping("/all/start")
    public KeyboardSession startAllMonitoring() {

        return monitorService.start(
                KeyboardMonitorMode.ALL
        );
    }

    @PostMapping("/{sessionId}/stop")
    public KeyboardSession stop(
            @PathVariable UUID sessionId
    ) {

        return monitorService.stop(sessionId);
    }

    @GetMapping("/{sessionId}")
    public KeyboardSession status(
            @PathVariable UUID sessionId
    ) {

        return monitorService.status(sessionId);
    }
}