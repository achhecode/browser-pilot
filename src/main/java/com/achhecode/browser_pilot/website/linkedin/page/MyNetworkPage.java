package com.achhecode.browser_pilot.website.linkedin.page;

import com.achhecode.browser_pilot.website.WebsitePage;
import com.achhecode.browser_pilot.website.linkedin.LinkedInGame;
import com.achhecode.browser_pilot.website.linkedin.LinkedInUrls;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class MyNetworkPage extends WebsitePage {

    private static final int ACTION_TIMEOUT = 10_000;

    public MyNetworkPage(Page page) {
        super(page);
    }

    public void open() {
        if (!isDisplayed()) {
            page.navigate(LinkedInUrls.MY_NETWORK);
        }
    }

    public boolean isDisplayed() {
        return page.url().contains("/mynetwork");
    }

    public void openGame(LinkedInGame game) {

    page.locator("a[href$='" + game.path() + "']")
                .first()
                .click(
                        new Locator.ClickOptions()
                                .setTimeout(ACTION_TIMEOUT)
                );
    }
}