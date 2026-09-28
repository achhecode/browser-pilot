package com.achhecode.browser_pilot.browser;

import org.springframework.stereotype.Component;

import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;

@Component 
public class BrowserPageResolver {

    private final PlaywrightManager playwrightManager;

    public BrowserPageResolver(PlaywrightManager playwrightManager) {
        this.playwrightManager = playwrightManager;
    }

    public Page findPage(String urlPrefix) {

        BrowserContext context =
                playwrightManager.getDefaultContext();

        return context.pages()
                .stream()
                .filter(page -> page.url().startsWith(urlPrefix))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalStateException(
                                "No browser page found for: " + urlPrefix
                        )
                );
    }

    public void waitUntilDocumentLoaded(Page page) {

        page.waitForLoadState(LoadState.DOMCONTENTLOADED);

        page.waitForFunction(
                "() => document.readyState === 'complete'"
        );
    }

    public Page findLinkedInPage() {
        return findPage("https://www.linkedin.com/");
    }
}