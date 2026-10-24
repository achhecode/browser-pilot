package com.achhecode.pinpointservice.page;

import com.achhecode.pinpointservice.PinpointanswerUrls;
import com.achhecode.playwrightservice.handler.WebsitePage;
import com.microsoft.playwright.Page;

public class PPHomePage extends WebsitePage {

    public PPHomePage(Page page) {
        super(page);
    }

    public boolean isDisplayed() {
        return page.url().equals(PinpointanswerUrls.HOME);
    }
}
