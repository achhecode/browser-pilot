package com.achhecode.browser_pilot.website.linkedin.service;

import com.achhecode.browser_pilot.browser.BrowserPageResolver;
import com.achhecode.browser_pilot.website.linkedin.LinkedInUrls;
import com.achhecode.browser_pilot.website.linkedin.page.MyNetworkPage;
import com.microsoft.playwright.Page;
import org.springframework.stereotype.Service;

@Service
public class LinkedInGameService {

    private final BrowserPageResolver pageResolver;

    public LinkedInGameService(
            BrowserPageResolver pageResolver
    ) {
        this.pageResolver = pageResolver;
    }

    private MyNetworkPage getMyNetworkPage() {

        Page page = pageResolver.findPage(
                LinkedInUrls.HOME
        );

        MyNetworkPage myNetworkPage =
                new MyNetworkPage(page);

        myNetworkPage.open();

        return myNetworkPage;
    }

    public void enterCrossclimb() {
        getMyNetworkPage().clickCrossclimb();
    }

    public void enterPatches() {
        getMyNetworkPage().clickPatches();
    }

    public void enterZip() {
        getMyNetworkPage().clickZip();
    }

    public void enterMiniSudoku() {
        getMyNetworkPage().clickMiniSudoku();
    }

    public void enterWend() {
        getMyNetworkPage().clickWend();
    }

    public void enterTango() {
        getMyNetworkPage().clickTango();
    }

    public void enterQueens() {
        getMyNetworkPage().clickQueens();
    }
}