package com.achhecode.linkedinservice.service;

import org.springframework.stereotype.Service;

import com.achhecode.browserpilotdto.dto.MiniSudokuRequest;
import com.achhecode.browserpilotdto.dto.QueensRequest;
import com.achhecode.browserpilotdto.dto.TangoRequest;
import com.achhecode.browserpilotdto.dto.ZipRequest;
import com.achhecode.linkedinservice.LinkedInGame;
import com.achhecode.linkedinservice.LinkedInPageResolver;
import com.achhecode.linkedinservice.page.ZipPage;
import com.achhecode.linkedinservice.page.games.MiniSudokuPage;
import com.achhecode.linkedinservice.page.games.PatchesPage;
import com.achhecode.linkedinservice.page.games.PinpointPage;
import com.achhecode.linkedinservice.page.games.QueensPage;
import com.achhecode.linkedinservice.page.games.TangoPage;
import com.achhecode.linkedinservice.page.games.WendPage;
import com.microsoft.playwright.Page;

@Service
public class LinkedInWebGameAutomationService {
    private final LinkedInPageResolver pageResolver;

    public LinkedInWebGameAutomationService(LinkedInPageResolver pageResolver) {
        this.pageResolver = pageResolver;
    }

    public void solveZip(ZipRequest request) {
        Page page = pageResolver.findPage();
        page.navigate(LinkedInGame.ZIP.url());
        ZipPage zipPage = new ZipPage(page);
        zipPage.solve(request);
    }

    public void solveQueens(QueensRequest request) {
        Page page = pageResolver.findPage();
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
