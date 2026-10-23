package com.achhecode.browser_pilot.website.linkedin.service;

import org.springframework.stereotype.Service;
import com.achhecode.browser_pilot.website.linkedin.LinkedInGame;
import com.achhecode.browser_pilot.website.linkedin.LinkedInPageResolver;
import com.achhecode.browser_pilot.website.linkedin.page.games.minisudoku.MiniSudokuPage;
import com.achhecode.browser_pilot.website.linkedin.page.games.minisudoku.MiniSudokuRequest;
import com.achhecode.browser_pilot.website.linkedin.page.games.patches.PatchesPage;
import com.achhecode.browser_pilot.website.linkedin.page.games.pinpoint.PinpointPage;
import com.achhecode.browser_pilot.website.linkedin.page.games.queens.QueensPage;
import com.achhecode.browser_pilot.website.linkedin.page.games.queens.QueensRequest;
import com.achhecode.browser_pilot.website.linkedin.page.games.tango.TangoPage;
import com.achhecode.browser_pilot.website.linkedin.page.games.tango.TangoRequest;
import com.achhecode.browser_pilot.website.linkedin.page.games.wend.WendPage;
import com.achhecode.browser_pilot.website.linkedin.page.games.zip.ZipPage;
import com.achhecode.browser_pilot.website.linkedin.page.games.zip.ZipRequest;
import com.microsoft.playwright.Page;

@Service
public class LinkedInWebGameAutomationService {
    private final LinkedInPageResolver pageResolver;

    public LinkedInWebGameAutomationService(LinkedInPageResolver pageResolver) {
        this.pageResolver = pageResolver;
    }

    public void solveZip(ZipRequest request) {
        Page page = pageResolver.getCurrentPage();
        page.navigate(LinkedInGame.ZIP.url());
        ZipPage zipPage = new ZipPage(page);
        zipPage.solve(request);
    }

    public void solveQueens(QueensRequest request) {
        Page page = pageResolver.getCurrentPage();
        page.navigate(LinkedInGame.QUEENS.url());
        QueensPage queensPage = new QueensPage(page);
        queensPage.solve(request.queenPositions());
    }

    public void solveMiniSudoku(MiniSudokuRequest request) {
        Page page = pageResolver.findPage();
        page.navigate(LinkedInGame.MINI_SUDOKU.url());
        MiniSudokuPage miniSudokuPage = new MiniSudokuPage(page);
        miniSudokuPage.solve(request);
    }

    public void solvePatches() {
        Page page = pageResolver.findPage();
        page.navigate(LinkedInGame.PATCHES.url());
        PatchesPage patchesPage = new PatchesPage(page);
        patchesPage.solve();
    }

    public void solvePinpoint() {
        Page page = pageResolver.findPage();
        page.navigate(LinkedInGame.PINPOINT.url());
        PinpointPage pinpointPage = new PinpointPage(page);
        pinpointPage.solve();
    }

    public void solveTango(TangoRequest request) {
        Page page = pageResolver.findPage();
        page.navigate(LinkedInGame.TANGO.url());
        TangoPage tangoPage = new TangoPage(page);
        tangoPage.solve(request);
    }

    public void solveWend() {
        Page page = pageResolver.findPage();
        page.navigate(LinkedInGame.WEND.url());
        WendPage wendPage = new WendPage(page);
        wendPage.solve();
    }
}
