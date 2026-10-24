package com.achhecode.linkedinservice.page;

import com.achhecode.linkedinservice.LinkedInGame;
import com.achhecode.linkedinservice.LinkedInUrls;
import com.achhecode.linkedinservice.handler.WebsitePage;
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
        page.locator("a[href$='" + game.path() + "']").first()
                .click(new Locator.ClickOptions().setTimeout(ACTION_TIMEOUT));
    }
}