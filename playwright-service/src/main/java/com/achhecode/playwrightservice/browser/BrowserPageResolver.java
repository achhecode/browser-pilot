package com.achhecode.playwrightservice.browser;

import java.util.function.Function;

import org.springframework.stereotype.Component;

import com.microsoft.playwright.Page;

@Component
public class BrowserPageResolver {

    private final TabManager tabManager;

    public BrowserPageResolver(TabManager tabManager) {
        this.tabManager = tabManager;
    }

    public BrowserTab findOrOpenPage(String url) {
        return tabManager.findOrOpenTab(url);
    }

    public BrowserTab getOrCreatePage() {
        return tabManager.getOrCreateTab();
    }

    public BrowserTab getFreshPage() {
        return tabManager.getFreshTab();
    }

    public BrowserTab getCurrentOpenPage() {
        return tabManager.getCurrentOpenTab();
    }

    public <T> T withPage(String url, Function<Page, T> operation) {
        BrowserTab tab = tabManager.findOrOpenTab(url);

        return tabManager.withTab(tab.getId(), operation);
    }

    public Page findOrOpenPlaywrightPage(String url) {
        BrowserTab tab = findOrOpenPage(url);

        return tabManager.withTab(tab.getId(), page -> page);
    }
}