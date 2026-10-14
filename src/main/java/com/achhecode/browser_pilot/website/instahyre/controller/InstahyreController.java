package com.achhecode.browser_pilot.website.instahyre.controller;

import com.achhecode.browser_pilot.website.instahyre.service.InstahyreAutomationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/instahyre")
@RequiredArgsConstructor 
public class InstahyreController {

    private final InstahyreAutomationService automationService;

    @PostMapping("/login")
    public ResponseEntity<String> login() {
        automationService.login();
        return ResponseEntity.ok("Instahyre login automation executed successfully");
    }


    @PostMapping("/recover")
    public ResponseEntity<String> recover() {
        automationService.openFreshTab();

        return ResponseEntity.ok(
                "Instahyre tab is ready"
        );
    }
}