package com.achhecode.browser_pilot.browser;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import org.springframework.stereotype.Component;

@Component
public class BrowserPageWaiter {

    public void waitUntilDocumentLoaded(Page page) {
        page.waitForLoadState(LoadState.DOMCONTENTLOADED);
        page.waitForFunction(
                "() => document.readyState === 'complete'"
        );
    }
}