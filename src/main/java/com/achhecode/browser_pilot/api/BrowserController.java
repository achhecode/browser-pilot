package com.achhecode.browser_pilot.api;

import com.achhecode.browser_pilot.browser.BrowserNavigationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/browser")
public class BrowserController {

    private final BrowserNavigationService navigationService;

    public BrowserController(
            BrowserNavigationService navigationService
    ) {
        this.navigationService = navigationService;
    }

    @PostMapping("/navigate")
    public ResponseEntity<Map<String, String>> navigate(
            @RequestBody NavigateRequest request
    ) {

        String finalUrl =
                navigationService.navigate(request.url());

        return ResponseEntity.ok(
                Map.of(
                        "status", "success",
                        "url", finalUrl
                )
        );
    }
}