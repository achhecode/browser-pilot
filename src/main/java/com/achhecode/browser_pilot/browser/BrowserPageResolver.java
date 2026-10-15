package com.achhecode.browser_pilot.browser;

import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BrowserPageResolver {

    private final PlaywrightManager playwrightManager;

    public Page findOrOpenPage(String url) {
        BrowserContext context = getContext();

        return context.pages()
                .stream()
                .filter(page -> !page.isClosed())
                .filter(page -> page.url().startsWith(url))
                .findFirst()
                .orElseGet(() -> {
                    Page page = getFreshPage();
                    page.navigate(url);
                    return page;
                });
    }

    public Page getOrCreatePage() {
        BrowserContext context = getContext();

        return context.pages()
                .stream()
                .filter(page -> !page.isClosed())
                .findFirst()
                .orElseGet(context::newPage);
    }

    /**
     * Returns a usable blank tab, or creates a new tab.
     * Existing website tabs are preserved.
     */
    public Page getFreshPage() {
        BrowserContext context = getContext();

        return context.pages()
                .stream()
                .filter(page -> !page.isClosed())
                .filter(this::isBlankPage)
                .findFirst()
                .orElseGet(context::newPage);
    }


    /**
     * Returns the most recently opened tab, regardless of its URL.
     * Creates a new tab if no open tabs exist.
     */
    public Page getCurrentOpenPage() {
        BrowserContext context = getContext();

        return context.pages()
                .stream()
                .filter(page -> !page.isClosed())
                .reduce((first, second) -> second)
                .orElseGet(context::newPage);
    }

    private boolean isBlankPage(Page page) {
        try {
            String url = page.url();
            return url.equals("about:blank") || url.equals("about:blank#");
        } catch (com.microsoft.playwright.PlaywrightException e) {
            return false;
        }
    }

    private BrowserContext getContext() {
        return playwrightManager.getOrCreateContext();
    }
}
