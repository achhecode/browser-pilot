package com.achhecode.browser_pilot.website.linkedin.service;

import com.achhecode.browser_pilot.browser.BrowserPageResolver;
import com.achhecode.browser_pilot.website.linkedin.LinkedInGame;
import com.achhecode.browser_pilot.website.linkedin.LinkedInUrls;
import com.achhecode.browser_pilot.website.linkedin.page.MyNetworkPage;
import com.achhecode.browser_pilot.website.linkedin.page.games.zip.ZipGameService;
import com.achhecode.browser_pilot.website.linkedin.page.games.zip.ZipPage;
import com.achhecode.browser_pilot.website.linkedin.page.games.zip.ZipRequest;
import com.microsoft.playwright.Page;
import org.springframework.stereotype.Service;

@Service
public class LinkedInGameService {

    private final BrowserPageResolver pageResolver;
    private final ZipGameService zipGameService;

    public LinkedInGameService(
            BrowserPageResolver pageResolver,
            ZipGameService zipGameService
    ) {
        this.pageResolver = pageResolver;
        this.zipGameService = zipGameService;
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
        getMyNetworkPage()
                .openGame(LinkedInGame.CROSSCLIMB);
    }

    public void enterPatches() {
        getMyNetworkPage()
                .openGame(LinkedInGame.PATCHES);
    }

    public void enterZip(ZipRequest request) {

        MyNetworkPage myNetworkPage =
                getMyNetworkPage();

        myNetworkPage.openGame(
                LinkedInGame.ZIP
        );

        ZipPage zipPage =
                new ZipPage(
                        myNetworkPage.page()
                );

        zipGameService.solve(
                zipPage,
                request
        );
    }

    public void enterMiniSudoku() {
        getMyNetworkPage()
                .openGame(LinkedInGame.MINI_SUDOKU);
    }

    public void enterWend() {
        getMyNetworkPage()
                .openGame(LinkedInGame.WEND);
    }

    public void enterTango() {
        getMyNetworkPage()
                .openGame(LinkedInGame.TANGO);
    }

    public void enterQueens() {
        getMyNetworkPage()
                .openGame(LinkedInGame.QUEENS);
    }
}