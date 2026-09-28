package com.achhecode.browser_pilot.website.linkedin.controller;

import com.achhecode.browser_pilot.website.linkedin.service.LinkedInAutomationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/linkedin")
public class LinkedInController {

    private final LinkedInAutomationService linkedInAutomationService;

    public LinkedInController(
            LinkedInAutomationService linkedInAutomationService
    ) {
        this.linkedInAutomationService =
                linkedInAutomationService;
    }

    @PostMapping("/home/my-network")
    public ResponseEntity<Void> clickMyNetwork() {

        linkedInAutomationService.clickMyNetwork();

        return ResponseEntity.ok().build();
    }
}