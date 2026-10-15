package com.achhecode.browser_pilot.website.instahyre.controller;

import com.achhecode.browser_pilot.api.AuditApi;
import com.achhecode.browser_pilot.website.instahyre.model.JobApplicationRequest;
import com.achhecode.browser_pilot.website.instahyre.service.InstahyreAutomationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/instahyre")
@RequiredArgsConstructor 
public class InstahyreController {

    private final InstahyreAutomationService automationService;

    @AuditApi ("INSTAHYRE_LOGIN")
    @PostMapping("/login")
    public ResponseEntity<String> login() {
        automationService.login();
        return ResponseEntity.ok("Instahyre login automation executed successfully");
    }


    @AuditApi ("INSTAHYRE_APPLY_JOB")
    @PostMapping("/apply-job")
    public ResponseEntity<String> applyJob(
            @Valid @RequestBody JobApplicationRequest request) {

        automationService.applyJob(request);
        return ResponseEntity.ok("Instahyre job applied successfully");
    }


    @AuditApi ("INSTAHYRE_RECOVER")
    @PostMapping("/recover")
    public ResponseEntity<String> recover() {
        automationService.openFreshTab();

        return ResponseEntity.ok(
                "Instahyre tab is ready"
        );
    }
}