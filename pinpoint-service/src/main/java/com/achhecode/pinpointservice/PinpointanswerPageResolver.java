package com.achhecode.pinpointservice;

import com.achhecode.pinpointservice.handler.WebsitePageResolver;
import com.achhecode.playwrightservice.browser.BrowserPageResolver;
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
