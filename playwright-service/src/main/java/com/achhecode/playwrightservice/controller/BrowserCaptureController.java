package com.achhecode.playwrightservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.achhecode.playwrightservice.capture.PageCaptureResult;
import com.achhecode.playwrightservice.capture.PageCaptureService;

@RestController
@RequestMapping("/api/browser")
public class BrowserCaptureController {

    private final PageCaptureService pageCaptureService;

    public BrowserCaptureController(
            PageCaptureService pageCaptureService
    ) {
        this.pageCaptureService = pageCaptureService;
    }

    @PostMapping("/capture")
    public ResponseEntity<PageCaptureResult> capture() {

        return ResponseEntity.ok(
                pageCaptureService.captureCurrentPage()
        );
    }
}