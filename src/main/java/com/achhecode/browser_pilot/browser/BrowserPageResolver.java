package com.achhecode.browser_pilot.browser;

import com.achhecode.browser_pilot.website.linkedin.LinkedInUrls;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import org.springframework.stereotype.Component;

@Component
public class BrowserPageResolver {

    private final PlaywrightManager playwrightManager;

    public BrowserPageResolver(PlaywrightManager playwrightManager) {
        this.playwrightManager = playwrightManager;
    }

    public Page findPage(String urlPrefix) {

        BrowserContext context =
                playwrightManager.getContext();

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

    public Page findLinkedInPage() {

        BrowserContext context =
                playwrightManager.getContext();

        return context.pages()
                .stream()
                .filter(page ->
                        page.url().startsWith(LinkedInUrls.LINKEDIN_URL_PREFIX)
                )
                .findFirst()
                .orElseThrow(() ->
                        new IllegalStateException(
                                "No LinkedIn browser page found"
                        )
                );
    }

    public void waitUntilDocumentLoaded(Page page) {

        page.waitForLoadState(LoadState.DOMCONTENTLOADED);

        page.waitForFunction(
                "() => document.readyState === 'complete'"
        );
    }
}