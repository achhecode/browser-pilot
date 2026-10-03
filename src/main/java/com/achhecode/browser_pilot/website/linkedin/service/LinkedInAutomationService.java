package com.achhecode.browser_pilot.website.linkedin.service;

import com.achhecode.browser_pilot.website.linkedin.LinkedInPageFactory;
import com.achhecode.browser_pilot.website.linkedin.LinkedInPageResolver;
import com.microsoft.playwright.Page;
import org.springframework.stereotype.Service;

@Service
public class LinkedInAutomationService {

    private final LinkedInPageResolver pageResolver;
    private final LinkedInPageFactory pageFactory;

    public LinkedInAutomationService(
            LinkedInPageResolver pageResolver,
            LinkedInPageFactory pageFactory
    ) {
        this.pageResolver = pageResolver;
        this.pageFactory = pageFactory;
    }

    public void clickMyNetwork() {

        Page page = pageResolver.findPage();

        // pageResolver.waitUntilDocumentLoaded(page);

        pageFactory
                .home(page)
                .clickMyNetwork();
    }
}