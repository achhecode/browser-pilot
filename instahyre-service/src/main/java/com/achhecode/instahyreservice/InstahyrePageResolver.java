package com.achhecode.instahyreservice;

import com.achhecode.instahyreservice.handler.WebsitePageResolver;
import com.achhecode.playwright.browser.BrowserPageResolver;
import com.microsoft.playwright.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InstahyrePageResolver implements WebsitePageResolver {

    private final BrowserPageResolver browserPageResolver;

    @Override
    public Page findPage() {
        return browserPageResolver.findOrOpenPage(InstahyreUrls.INSTAHYRE_URL_PREFIX);
    }
}