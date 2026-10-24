package com.achhecode.instahyreservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.achhecode.instahyreservice.model.JobApplicationRequest;
import com.achhecode.instahyreservice.service.InstahyreAutomationService;
import com.achhecode.instahyreservice.service.InstahyreQuestionnaireService;

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
}