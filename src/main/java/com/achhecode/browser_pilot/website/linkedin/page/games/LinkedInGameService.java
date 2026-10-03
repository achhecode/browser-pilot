package com.achhecode.browser_pilot.website.linkedin.page.games;

import com.achhecode.browser_pilot.website.linkedin.LinkedInGame;
import com.achhecode.browser_pilot.website.linkedin.LinkedInPageResolver;
import com.achhecode.browser_pilot.website.linkedin.page.MyNetworkPage;
import com.achhecode.browser_pilot.website.linkedin.page.games.minisudoku.MiniSudokuPage;
import com.achhecode.browser_pilot.website.linkedin.page.games.crossclimb.CrossclimbGameService;
import com.achhecode.browser_pilot.website.linkedin.page.games.crossclimb.CrossclimbPage;
import com.achhecode.browser_pilot.website.linkedin.page.games.crossclimb.CrossclimbRequest;
import com.achhecode.browser_pilot.website.linkedin.page.games.minisudoku.MiniSudokuGameService;
import com.achhecode.browser_pilot.website.linkedin.page.games.minisudoku.MiniSudokuRequest;
import com.achhecode.browser_pilot.website.linkedin.page.games.patches.PatchesGameService;
import com.achhecode.browser_pilot.website.linkedin.page.games.patches.PatchesPage;
import com.achhecode.browser_pilot.website.linkedin.page.games.patches.PatchesRequest;
import com.achhecode.browser_pilot.website.linkedin.page.games.pinpoint.PinpointGameService;
import com.achhecode.browser_pilot.website.linkedin.page.games.pinpoint.PinpointPage;
import com.achhecode.browser_pilot.website.linkedin.page.games.pinpoint.PinpointRequest;
import com.achhecode.browser_pilot.website.linkedin.page.games.queen.QueensGameService;
import com.achhecode.browser_pilot.website.linkedin.page.games.queen.QueensPage;
import com.achhecode.browser_pilot.website.linkedin.page.games.queen.QueensRequest;
import com.achhecode.browser_pilot.website.linkedin.page.games.tango.TangoGameService;
import com.achhecode.browser_pilot.website.linkedin.page.games.tango.TangoPage;
import com.achhecode.browser_pilot.website.linkedin.page.games.tango.TangoRequest;
import com.achhecode.browser_pilot.website.linkedin.page.games.wend.WendGameService;
import com.achhecode.browser_pilot.website.linkedin.page.games.wend.WendPage;
import com.achhecode.browser_pilot.website.linkedin.page.games.wend.WendRequest;
import com.achhecode.browser_pilot.website.linkedin.page.games.zip.ZipGameService;
import com.achhecode.browser_pilot.website.linkedin.page.games.zip.ZipPage;
import com.achhecode.browser_pilot.website.linkedin.page.games.zip.ZipRequest;
import com.microsoft.playwright.Page;

import lombok.extern.slf4j.Slf4j;

import java.util.function.Function;

import org.springframework.stereotype.Service;

@Slf4j 
@Service
public class LinkedInGameService {

    private final LinkedInPageResolver pageResolver;
    private final ZipGameService zipGameService;
    private final MiniSudokuGameService sudokuGameService;
    private final QueensGameService queensGameService;
    private final TangoGameService tangoGameService;
    private final PatchesGameService patchesGameService;
    private final WendGameService wendGameService;
    private final PinpointGameService pinpointGameService;
    private final CrossclimbGameService crossclimbGameService;

    public LinkedInGameService(
            LinkedInPageResolver pageResolver,
            ZipGameService zipGameService,
            MiniSudokuGameService sudokuGameService,
            QueensGameService queensGameService,
            TangoGameService tangoGameService,
            PatchesGameService patchesGameService,
            WendGameService wendGameService,
            PinpointGameService pinpointGameService,
            CrossclimbGameService crossclimbGameService
    ) {
        this.pageResolver = pageResolver;
        this.zipGameService = zipGameService;
        this.sudokuGameService = sudokuGameService;
        this.queensGameService = queensGameService;
        this.tangoGameService = tangoGameService;
        this.patchesGameService = patchesGameService;
        this.wendGameService = wendGameService;
        this.pinpointGameService = pinpointGameService;
        this.crossclimbGameService = crossclimbGameService;
    }

    private <R extends LinkedInGameRequest, P> void enterGame(
        LinkedInGame game,
        R request,
        LinkedInGameSolver<R, P> solver,
        Function<Page, P> pageFactory
    ) {
        if (request.onlyKey()) {
            solver.solve(request);
            return;
        }

        MyNetworkPage networkPage = openGame(game);

        P gamePage = pageFactory.apply(networkPage.page());

        solver.solve(gamePage, request);
    }

    private MyNetworkPage getMyNetworkPage() {
        Page page = pageResolver.findPage();

        MyNetworkPage myNetworkPage = new MyNetworkPage(page);
        myNetworkPage.open();

        return myNetworkPage;
    }

    private MyNetworkPage openGame(LinkedInGame game) {

        Page page = pageResolver.findPage();

        log.info("Currently at page {}", page.url());

        if (page.url().contains(game.path())) {
            return new MyNetworkPage(page);
        }

        MyNetworkPage myNetworkPage = getMyNetworkPage();

        log.info("Navigating to game");
        myNetworkPage.openGame(game);

        return myNetworkPage;
    }

    public void enterCrossclimb(CrossclimbRequest request) {
        enterGame(
                LinkedInGame.CROSSCLIMB,
                request,
                crossclimbGameService,
                CrossclimbPage::new
        );
    }

    public void enterPatches(PatchesRequest request) {
        enterGame(
                LinkedInGame.PATCHES,
                request,
                patchesGameService,
                PatchesPage::new
        );
    }

    public void enterZip(ZipRequest request) {
        enterGame(
                LinkedInGame.ZIP,
                request,
                zipGameService,
                ZipPage::new
        );
    }

    public void enterMiniSudoku(MiniSudokuRequest request) {
        enterGame(
                LinkedInGame.MINI_SUDOKU,
                request,
                sudokuGameService,
                MiniSudokuPage::new
        );
    }

    public void enterWend(WendRequest request) {
        enterGame(
                LinkedInGame.WEND,
                request,
                wendGameService,
                WendPage::new
        );
    }

    public void enterTango(TangoRequest request) {
        enterGame(
                LinkedInGame.TANGO,
                request,
                tangoGameService,
                TangoPage::new
        );
    }

    public void enterQueens(QueensRequest request) {
        enterGame(
                LinkedInGame.QUEENS,
                request,
                queensGameService,
                QueensPage::new
        );
    }

    public void enterPinpoint(PinpointRequest request) {
        enterGame(
                LinkedInGame.PINPOINT,
                request,
                pinpointGameService,
                PinpointPage::new
        );
    }
}