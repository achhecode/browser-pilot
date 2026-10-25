package com.achhecode.linkedinservice;

import com.achhecode.linkedinservice.handler.WebsitePageResolver;
import com.achhecode.playwright.browser.BrowserPageResolver;
import com.microsoft.playwright.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor 
public class LinkedInPageResolver implements WebsitePageResolver {

    private final BrowserPageResolver browserPageResolver;

    @Override
    public Page findPage() {
        return browserPageResolver.findOrOpenPage(LinkedInUrls.LINKEDIN_URL_PREFIX);
    }
}