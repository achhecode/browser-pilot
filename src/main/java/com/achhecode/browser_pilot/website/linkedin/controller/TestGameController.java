package com.achhecode.browser_pilot.website.linkedin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.achhecode.browser_pilot.website.linkedin.page.games.zip.ZipCommandService;
import com.achhecode.browser_pilot.website.linkedin.page.games.zip.ZipRequest;

import jakarta.validation.Valid;

@RestController
@RequestMapping ("/api/test/linkedin/games")
public class TestGameController {
    private final ZipCommandService zipCommandService;

    public TestGameController(ZipCommandService zipCommandService){
        this.zipCommandService = zipCommandService;
    }

    @PostMapping("/zip")
    public ResponseEntity<Void> run(
            @Valid @RequestBody ZipRequest request
    ) {
        zipCommandService.executeCommand(request);
        return ResponseEntity.ok().build();
    }
}
