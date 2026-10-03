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
        return getContext()
                .pages()
                .stream()
                .filter(page -> !page.isClosed())
                .filter(page -> page.url().startsWith(url))
                .findFirst()
                .orElseGet(() -> openPage(url));
    }

    public Page getOrCreatePage() {
        return getContext()
                .pages()
                .stream()
                .filter(page -> !page.isClosed())
                .findFirst()
                .orElseGet(() -> getContext().newPage());
    }

    private Page openPage(String url) {
        Page page = getContext().newPage();
        page.navigate(url);
        return page;
    }

    private BrowserContext getContext() {
        return playwrightManager.getContext();
    }
}