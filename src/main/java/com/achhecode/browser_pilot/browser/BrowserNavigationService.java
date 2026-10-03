package com.achhecode.browser_pilot.browser;

import com.microsoft.playwright.Page;
import org.springframework.stereotype.Service;

@Service
public class BrowserNavigationService {

    private final PlaywrightManager playwrightManager;

    public BrowserNavigationService(
            PlaywrightManager playwrightManager
    ) {
        this.playwrightManager = playwrightManager;
    }

    public String navigate(String url) {

        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException(
                    "URL must not be empty."
            );
        }

        Page page = playwrightManager.getOrCreatePage();

        page.navigate(url);

        return page.url();
    }
}