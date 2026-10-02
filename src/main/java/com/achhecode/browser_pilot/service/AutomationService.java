package com.achhecode.browser_pilot.service;

import com.achhecode.browser_pilot.browser.PlaywrightManager;
import com.achhecode.browser_pilot.dto.AutomationRequest;
import com.achhecode.browser_pilot.dto.AutomationResponse;
import com.microsoft.playwright.Page;
import org.springframework.stereotype.Service;

@Service
public class AutomationService {

    private final PlaywrightManager playwrightManager;

    public AutomationService(
            PlaywrightManager playwrightManager
    ) {
        this.playwrightManager = playwrightManager;
    }

    public AutomationResponse run(
            AutomationRequest request
    ) {

        Page page =
                playwrightManager.getOrCreatePage();

        page.navigate(request.url());

        return new AutomationResponse(
                request.url(),
                page.title()
        );
    }
}