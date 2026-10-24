package com.achhecode.pinpointservice;

import com.achhecode.playwrightservice.browser.BrowserPageResolver;
import com.achhecode.playwrightservice.handler.WebsitePageResolver;
import com.microsoft.playwright.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PinpointanswerPageResolver implements WebsitePageResolver {

    private final BrowserPageResolver browserPageResolver;

    @Override
    public Page findPage() {
        return browserPageResolver.findOrOpenPlaywrightPage(PinpointanswerUrls.URL_PREFIX);
    }
}
