package com.achhecode.browser_pilot.website.linkedin.service;

import com.achhecode.browser_pilot.browser.BrowserPageResolver;
import com.achhecode.browser_pilot.website.linkedin.LinkedInPageFactory;
import com.microsoft.playwright.Page;
import org.springframework.stereotype.Service;

@Service
public class LinkedInAutomationService {

    private final BrowserPageResolver pageResolver;
    private final LinkedInPageFactory pageFactory;

    public LinkedInAutomationService(
            BrowserPageResolver pageResolver,
            LinkedInPageFactory pageFactory
    ) {
        this.pageResolver = pageResolver;
        this.pageFactory = pageFactory;
    }

    public void clickMyNetwork() {

        Page page = pageResolver.findLinkedInPage();

        // pageResolver.waitUntilDocumentLoaded(page);

        pageFactory
                .home(page)
                .clickMyNetwork();
    }
}