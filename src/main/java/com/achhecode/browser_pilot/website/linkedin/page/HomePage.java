package com.achhecode.browser_pilot.website.linkedin.page;

import com.achhecode.browser_pilot.website.WebsitePage;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class HomePage extends WebsitePage {

    public HomePage(Page page) {
        super(page);
    }

    public void clickMyNetwork() {

        Locator myNetwork =
            page.locator(
                    "a[href='https://www.linkedin.com/mynetwork']"
            ).first();

        myNetwork.waitFor();

        myNetwork.click();

        // page.locator("a[aria-label^='My Network']").click();
    }

    public boolean hasNotifications() {
        // Implement later
        return false;
    }
}