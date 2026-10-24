package com.achhecode.linkedinservice.handler;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;

public abstract class WebsitePage {

    protected final Page page;

    public abstract boolean isDisplayed();

    public void waitUntilLoaded() {
        page.waitForLoadState(LoadState.LOAD);
    }

    protected WebsitePage(Page page) {
        this.page = page;
    }

    public Page page() {
        return page;
    }

    public String url() {
        return page.url();
    }

    public String title() {
        return page.title();
    }

    public void navigateTo(String url) {
        page.navigate(url);
    }
}