package com.achhecode.browser_pilot.controller;

import com.achhecode.browser_pilot.capture.PageCaptureResult;
import com.achhecode.browser_pilot.capture.PageCaptureService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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