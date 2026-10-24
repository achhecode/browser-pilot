package com.achhecode.pinpointservice.page;

import com.achhecode.pinpointservice.PinpointanswerUrls;
import com.achhecode.playwrightservice.handler.WebsitePage;
import com.microsoft.playwright.Page;

public class PPPage extends WebsitePage {

    public PPPage(Page page) {
        super(page);
    }

    public boolean isDisplayed() {
        return page.url().contains(PinpointanswerUrls.URL_PREFIX);
    }

}