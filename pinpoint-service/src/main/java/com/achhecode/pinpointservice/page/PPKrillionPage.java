package com.achhecode.pinpointservice.page;

import com.achhecode.pinpointservice.PinpointanswerUrls;
import com.achhecode.playwrightservice.handler.WebsitePage;
import com.microsoft.playwright.Page;

public class PPKrillionPage extends WebsitePage implements AnswerPage<String> {

    public PPKrillionPage(Page page) {
        super(page);
    }

    public boolean isDisplayed() {
        return page.url().contains(PinpointanswerUrls.KRILLION);
    }

    public String getAnswer() {
        return "";
    }

}