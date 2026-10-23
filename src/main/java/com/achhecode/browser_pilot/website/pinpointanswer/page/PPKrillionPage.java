package com.achhecode.browser_pilot.website.pinpointanswer.page;

import com.achhecode.browser_pilot.website.WebsitePage;
import com.achhecode.browser_pilot.website.pinpointanswer.PinpointanswerUrls;
import com.microsoft.playwright.Page;

public class PPKrillionPage extends WebsitePage implements AnswerPage<String>{

    public PPKrillionPage(Page page) {
        super(page);
    }

    public boolean isDisplayed(){
        return page.url().contains(PinpointanswerUrls.KRILLION);
    }

    public String getAnswer(){
        return "";
    }
    
}