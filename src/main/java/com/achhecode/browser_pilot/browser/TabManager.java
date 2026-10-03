package com.achhecode.browser_pilot.browser;

import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TabManager {

    private final PlaywrightManager playwrightManager;

    private final Map<String, BrowserTab> tabs =
            new ConcurrentHashMap<>();

    public TabManager(
            PlaywrightManager playwrightManager
    ) {
        this.playwrightManager = playwrightManager;
    }

    public BrowserTab getTab() {
        return getTab(0);
    }

    public BrowserTab getTab(int index) {

        syncTabs();

        BrowserContext context =
                playwrightManager.getDefaultContext();

        List<Page> pages = context.pages();

        if (pages.isEmpty()) {
            throw new IllegalStateException(
                    "No browser tabs are currently open."
            );
        }

        if (index < 0 || index >= pages.size()) {
            throw new IllegalArgumentException(
                    "Invalid tab index: " + index
                            + ". Available tabs: "
                            + pages.size()
            );
        }

        Page page = pages.get(index);

        return findByPage(page);
    }

    public BrowserTab getTab(String tabId) {

        syncTabs();

        BrowserTab tab = tabs.get(tabId);

        if (tab == null) {
            throw new NoSuchElementException(
                    "Tab not found: " + tabId
            );
        }

        if (tab.isClosed()) {

            tabs.remove(tabId);

            throw new NoSuchElementException(
                    "Tab is closed: " + tabId
            );
        }

        return tab;
    }

    public BrowserTab createTab() {

        BrowserContext context =
                playwrightManager.getDefaultContext();

        Page page = context.newPage();

        String tabId =
                "tab-" + UUID.randomUUID();

        BrowserTab tab =
                new BrowserTab(tabId, page);

        tabs.put(tabId, tab);

        return tab;
    }

    public List<BrowserTab> getTabs() {

        syncTabs();

        BrowserContext context =
                playwrightManager.getDefaultContext();

        List<BrowserTab> result =
                new ArrayList<>();

        for (Page page : context.pages()) {

            BrowserTab tab =
                    findByPage(page);

            result.add(tab);
        }

        return result;
    }

    public void closeTab(String tabId) {

        BrowserTab tab =
                getTab(tabId);

        tab.getPage().close();

        tabs.remove(tabId);
    }

    public void closeTab(int index) {

        BrowserTab tab =
                getTab(index);

        closeTab(tab.getId());
    }

    private BrowserTab findByPage(Page page) {

        return tabs.values()
                .stream()
                .filter(tab ->
                        tab.getPage() == page
                )
                .findFirst()
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Browser tab is not registered."
                        )
                );
    }

    private void syncTabs() {

        BrowserContext context =
                playwrightManager.getDefaultContext();

        Set<Page> currentPages =
                Collections.newSetFromMap(
                        new IdentityHashMap<>()
                );

        currentPages.addAll(context.pages());

        /*
         * Remove closed/removed tabs.
         */
        tabs.entrySet().removeIf(entry -> {

            Page page =
                    entry.getValue().getPage();

            return page.isClosed()
                    || !currentPages.contains(page);
        });

        /*
         * Discover tabs that may have been created
         * outside BrowserPilot.
         *
         * Particularly useful in ATTACH mode.
         */
        for (Page page : currentPages) {

            boolean known =
                    tabs.values()
                            .stream()
                            .anyMatch(
                                    tab ->
                                            tab.getPage() == page
                            );

            if (!known) {

                String tabId =
                        "tab-" + UUID.randomUUID();

                tabs.put(
                        tabId,
                        new BrowserTab(tabId, page)
                );
            }
        }
    }
}