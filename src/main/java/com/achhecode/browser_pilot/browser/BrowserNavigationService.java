package com.achhecode.browser_pilot.browser;

import org.springframework.stereotype.Service;

@Service
public class BrowserNavigationService {

    private final TabManager tabManager;

    public BrowserNavigationService(
            TabManager tabManager
    ) {
        this.tabManager = tabManager;
    }

    public BrowserTab navigate(
            String tabId,
            String url
    ) {

        BrowserTab tab =
                tabManager.getTab(tabId);

        tab.getPage().navigate(url);

        return tab;
    }

    public BrowserTab navigate(
            int tabIndex,
            String url
    ) {

        BrowserTab tab =
                tabManager.getTab(tabIndex);

        tab.getPage().navigate(url);

        return tab;
    }

    public BrowserTab navigate(
            String url
    ) {

        return navigate(0, url);
    }
}