package com.achhecode.browser_pilot.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import com.achhecode.browser_pilot.dto.AutomationRequest;
import com.achhecode.browser_pilot.dto.AutomationResponse;
import com.achhecode.browser_pilot.service.AutomationService;

@RestController
@RequestMapping("/api/automation")
public class AutomationController {

    private final AutomationService automationService;

    public AutomationController(
            AutomationService automationService
    ) {
        this.automationService = automationService;
    }

    @PostMapping("/run")
    public AutomationResponse run(
            @Valid @RequestBody AutomationRequest request
    ) {

        return automationService.run(request);
    }
}