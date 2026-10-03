package com.achhecode.browser_pilot.browser;

import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BrowserPageResolver {

    private final PlaywrightManager playwrightManager;

    public Page findPage(String url) {
        // return getContext()
        //         .pages()
        //         .stream()
        //         .filter(page -> page.url().startsWith(url))
        //         .findFirst()
        //         .orElseThrow(() ->
        //                 new IllegalStateException(
        //                         "No browser page found for: " + urlPrefix
        //                 )
        //         );

        return getContext()
            .pages()
            .stream()
            .filter(page -> page.url().startsWith(url))
            .findFirst()
            .orElseGet(() -> openNewPage(url));
    }

    private Page openNewPage(String url) {
        Page page = getContext().newPage();
        page.navigate(url);
        return page;
    }

    private BrowserContext getContext() {
        return playwrightManager.getContext();
    }
}