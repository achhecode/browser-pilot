package com.achhecode.browser_pilot.website.instahyre.controller;

import com.achhecode.browser_pilot.website.instahyre.model.JobApplicationRequest;
import com.achhecode.browser_pilot.website.instahyre.service.InstahyreAutomationService;
import com.achhecode.browser_pilot.website.instahyre.service.InstahyreQuestionnaireService;

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
    private final InstahyreQuestionnaireService instahyreQuestionnaireService;

    @PostMapping("/login")
    public ResponseEntity<String> login() {
        automationService.login();
        return ResponseEntity.ok("Instahyre login automation executed successfully");
    }

    @PostMapping("/apply-job")
    public ResponseEntity<String> applyJob(
            @Valid @RequestBody JobApplicationRequest request) {

        automationService.applyJob(request);
        return ResponseEntity.ok("Instahyre job applied successfully");
    }

    @PostMapping("/questionnaire/complete")
    public ResponseEntity<String> completeQuestionnaire() {
        instahyreQuestionnaireService.completeQuestionnaire();

        return ResponseEntity.ok(
                "Questionnaire completed and submitted."
        );
    }

    @PostMapping("/recover")
    public ResponseEntity<String> recover() {
        automationService.openFreshTab();

        return ResponseEntity.ok(
                "Instahyre tab is ready"
        );
    }
}