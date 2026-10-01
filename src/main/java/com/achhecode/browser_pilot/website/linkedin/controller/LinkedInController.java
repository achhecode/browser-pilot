package com.achhecode.browser_pilot.website.linkedin.controller;

import com.achhecode.browser_pilot.website.linkedin.service.LinkedInAutomationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/linkedin")
@Tag(
        name = "LinkedIn - Game",
        description = "Automation APIs for the LinkedIn game."
)
public class LinkedInController {

    private final LinkedInAutomationService linkedInAutomationService;

    public LinkedInController(
            LinkedInAutomationService linkedInAutomationService
    ) {
        this.linkedInAutomationService =
                linkedInAutomationService;
    }

    @Operation(
            summary = "LnkedIn My Network Navigation",
            description = "Navigate to my network on LinkedIn page."
    )
    @PostMapping("/home/my-network")
    public ResponseEntity<Void> clickMyNetwork() {

        linkedInAutomationService.clickMyNetwork();

        return ResponseEntity.ok().build();
    }
}