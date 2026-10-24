package com.achhecode.playwrightservice.browser;

import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;

public record PageHandle(
        BrowserContext context,
        Page page
) implements AutoCloseable {

    @Override
    public void close() {
        context.close();
    }
}