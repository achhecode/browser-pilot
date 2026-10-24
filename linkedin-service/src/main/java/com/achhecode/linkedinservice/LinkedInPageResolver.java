package com.achhecode.linkedinservice;

import com.achhecode.playwrightservice.browser.BrowserPageResolver;
import com.achhecode.playwrightservice.handler.WebsitePageResolver;
import com.microsoft.playwright.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor 
public class LinkedInPageResolver implements WebsitePageResolver{

    private final BrowserPageResolver browserPageResolver;

    @Override
    public Page findPage() {
        return browserPageResolver.findOrOpenPlaywrightPage(LinkedInUrls.LINKEDIN_URL_PREFIX);
    }
}