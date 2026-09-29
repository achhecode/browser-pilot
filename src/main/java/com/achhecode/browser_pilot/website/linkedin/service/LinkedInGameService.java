package com.achhecode.browser_pilot.website.linkedin.service;

import com.achhecode.browser_pilot.browser.BrowserPageResolver;
import com.achhecode.browser_pilot.website.linkedin.LinkedInGame;
import com.achhecode.browser_pilot.website.linkedin.LinkedInUrls;
import com.achhecode.browser_pilot.website.linkedin.page.MyNetworkPage;
import com.achhecode.browser_pilot.website.linkedin.page.games.minisudoku.MiniSudokuPage;
import com.achhecode.browser_pilot.website.linkedin.page.games.minisudoku.MiniSudokuGameService;
import com.achhecode.browser_pilot.website.linkedin.page.games.minisudoku.MiniSudokuRequest;
import com.achhecode.browser_pilot.website.linkedin.page.games.zip.ZipGameService;
import com.achhecode.browser_pilot.website.linkedin.page.games.zip.ZipPage;
import com.achhecode.browser_pilot.website.linkedin.page.games.zip.ZipRequest;
import com.microsoft.playwright.Page;
import org.springframework.stereotype.Service;

@Service
public class LinkedInGameService {

    private final BrowserPageResolver pageResolver;
    private final ZipGameService zipGameService;
    private final MiniSudokuGameService sudokuGameService;

    public LinkedInGameService(
            BrowserPageResolver pageResolver,
            ZipGameService zipGameService,
            MiniSudokuGameService sudokuGameService
    ) {
        this.pageResolver = pageResolver;
        this.zipGameService = zipGameService;
        this.sudokuGameService = sudokuGameService;
    }

    private MyNetworkPage getMyNetworkPage() {
        Page page = pageResolver.findPage(LinkedInUrls.HOME);

        MyNetworkPage myNetworkPage = new MyNetworkPage(page);
        myNetworkPage.open();

        return myNetworkPage;
    }

    private MyNetworkPage openGame(LinkedInGame game) {

        Page page = pageResolver.findPage(LinkedInUrls.HOME);

        if (page.url().contains(game.path())) {
            return new MyNetworkPage(page);
        }

        MyNetworkPage myNetworkPage = getMyNetworkPage();

        myNetworkPage.openGame(game);

        return myNetworkPage;
    }

    public void enterCrossclimb() {
        openGame(LinkedInGame.CROSSCLIMB);
    }

    public void enterPatches() {
        openGame(LinkedInGame.PATCHES);
    }

    public void enterZip(ZipRequest zipRequest) {
        MyNetworkPage page = openGame(LinkedInGame.ZIP);

        ZipPage zipPage = new ZipPage(page.page());

        zipGameService.solve(
                zipPage,
                zipRequest
        );
    }

    public void enterMiniSudoku(MiniSudokuRequest miniSudokuRequest) {
        MyNetworkPage page = openGame(LinkedInGame.MINI_SUDOKU);

        MiniSudokuPage miniSudokuPage = new MiniSudokuPage(page.page());

        sudokuGameService.solve(
                miniSudokuPage,
                miniSudokuRequest
        );
    }

    public void enterWend() {
        openGame(LinkedInGame.WEND);
    }

    public void enterTango() {
        openGame(LinkedInGame.TANGO);
    }

    public void enterQueens() {
        openGame(LinkedInGame.QUEENS);
    }
}