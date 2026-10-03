package com.achhecode.browser_pilot.website.linkedin.service;

import com.achhecode.browser_pilot.website.linkedin.LinkedInGame;
import com.achhecode.browser_pilot.website.linkedin.LinkedInPageResolver;
import com.achhecode.browser_pilot.website.linkedin.page.MyNetworkPage;
import com.achhecode.browser_pilot.website.linkedin.page.games.minisudoku.MiniSudokuPage;
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

    public LinkedInGameService(
            LinkedInPageResolver pageResolver,
            ZipGameService zipGameService,
            MiniSudokuGameService sudokuGameService,
            QueensGameService queensGameService,
            TangoGameService tangoGameService,
            PatchesGameService patchesGameService,
            WendGameService wendGameService,
            PinpointGameService pinpointGameService
    ) {
        this.pageResolver = pageResolver;
        this.zipGameService = zipGameService;
        this.sudokuGameService = sudokuGameService;
        this.queensGameService = queensGameService;
        this.tangoGameService = tangoGameService;
        this.patchesGameService = patchesGameService;
        this.wendGameService = wendGameService;
        this.pinpointGameService = pinpointGameService;
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

    public void enterCrossclimb() {
        openGame(LinkedInGame.CROSSCLIMB);
    }

    public void enterPatches(PatchesRequest request) {
        if(request.onlyKey()){
            patchesGameService.solve(
                    request
            );
        }else{
            MyNetworkPage page = openGame(LinkedInGame.PATCHES);

            PatchesPage patchesPage = new PatchesPage(page.page());

            patchesGameService.solve(
                    patchesPage,
                    request
            );
        }
    }

    public void enterZip(ZipRequest zipRequest) {
        if(zipRequest.onlyKey()){
            zipGameService.solve(
                    zipRequest
            );
        }else{
            MyNetworkPage page = openGame(LinkedInGame.ZIP);

            ZipPage zipPage = new ZipPage(page.page());

            zipGameService.solve(
                    zipPage,
                    zipRequest
            );
        }
        
    }

    public void enterMiniSudoku(MiniSudokuRequest miniSudokuRequest) {
        if(miniSudokuRequest.onlyKey()){
            sudokuGameService.solve(
                    miniSudokuRequest
            );
        }else{
            MyNetworkPage page = openGame(LinkedInGame.MINI_SUDOKU);

            MiniSudokuPage miniSudokuPage = new MiniSudokuPage(page.page());

            sudokuGameService.solve(
                    miniSudokuPage,
                    miniSudokuRequest
            );
        }
    }

    public void enterWend(WendRequest request) {
        if(request.onlyKey()){
            wendGameService.solve(
                    request
            );
        }else{
            MyNetworkPage page = openGame(LinkedInGame.WEND);

            WendPage wendPage = new WendPage(page.page());

            wendGameService.solve(
                    wendPage,
                    request
            );
        }
    }

    public void enterTango(TangoRequest tangoRequest) {
        if(tangoRequest.onlyKey()){
            tangoGameService.solve(
                    tangoRequest
            );
        }else{
            MyNetworkPage page = openGame(LinkedInGame.TANGO);

            TangoPage tangoPage = new TangoPage(page.page());

            tangoGameService.solve(
                    tangoPage,
                    tangoRequest
            );
        }
    }

    public void enterQueens(QueensRequest queensRequest) {
        if(queensRequest.onlyKey()){
            queensGameService.solve(
                    queensRequest
            );
        }else{
            MyNetworkPage page = openGame(LinkedInGame.QUEENS);

            QueensPage queensPage = new QueensPage(page.page());

            queensGameService.solve(
                    queensPage,
                    queensRequest
            );
        }
        
    }

    // 
    public void enterPinpoint(PinpointRequest request) {
        if(request.onlyKey()){
            pinpointGameService.solve(
                    request
            );
        }else{
            MyNetworkPage networkPage = openGame(LinkedInGame.PINPOINT);

            PinpointPage page = new PinpointPage(networkPage.page());

            pinpointGameService.solve(
                    page,
                    request
            );
        }
    }
}