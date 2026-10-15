package com.achhecode.browser_pilot.website.instahyre;

import com.achhecode.browser_pilot.browser.BrowserPageResolver;
import com.achhecode.browser_pilot.website.WebsitePageResolver;
import com.microsoft.playwright.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InstahyrePageResolver implements WebsitePageResolver {

    private final BrowserPageResolver browserPageResolver;

    @Override
    public Page findPage() {
        return browserPageResolver.findOrOpenPage(
                InstahyreUrls.INSTAHYRE_URL_PREFIX
        );
    }

    public Page openFreshPage() {
        Page page = browserPageResolver.getFreshPage();

        if (!page.url().startsWith(InstahyreUrls.INSTAHYRE_URL_PREFIX)) {
            page.navigate(InstahyreUrls.INSTAHYRE_URL_PREFIX);
        }

        return page;
    }

    public Page getQuestionnairePage() {
        Page page = browserPageResolver.getCurrentOpenPage();

        if (!page.url().startsWith(InstahyreUrls.QUESTIONNAIRE)) {
            return findPage();
        }

        return page;
    }
}