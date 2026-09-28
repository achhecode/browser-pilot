package com.achhecode.browser_pilot.website;

import com.microsoft.playwright.Page;

public abstract class WebsitePage {

    protected final Page page;

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
}