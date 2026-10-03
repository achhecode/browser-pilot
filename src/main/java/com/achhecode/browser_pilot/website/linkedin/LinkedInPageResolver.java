package com.achhecode.browser_pilot.website.linkedin;

import com.achhecode.browser_pilot.browser.BrowserPageResolver;
import com.achhecode.browser_pilot.website.WebsitePageResolver;
import com.microsoft.playwright.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor 
public class LinkedInPageResolver implements WebsitePageResolver{

    private final BrowserPageResolver browserPageResolver;

    @Override
    public Page findPage() {
        return browserPageResolver.findPage(
                LinkedInUrls.LINKEDIN_URL_PREFIX
        );
    }
}