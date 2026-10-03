package com.achhecode.browser_pilot.browser;

import com.microsoft.playwright.Page;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BrowserTab {

    private final String id;
    private final Page page;

    public String getUrl() {
        return page.url();
    }

    public String getTitle() {
        return page.isClosed()
                ? ""
                : page.title();
    }

    public boolean isClosed() {
        return page.isClosed();
    }
}