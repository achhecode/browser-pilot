package com.achhecode.browser_pilot.website.linkedin.service;

import com.achhecode.browser_pilot.browser.BrowserPageResolver;
import com.achhecode.browser_pilot.website.linkedin.LinkedInGame;
import com.achhecode.browser_pilot.website.linkedin.LinkedInUrls;
import com.achhecode.browser_pilot.website.linkedin.page.MyNetworkPage;
import com.achhecode.browser_pilot.website.linkedin.page.games.minisudoku.MiniSudokuPage;
import com.achhecode.browser_pilot.website.linkedin.page.games.minisudoku.MiniSudokuGameService;
import com.achhecode.browser_pilot.website.linkedin.page.games.minisudoku.MiniSudokuRequest;
import com.achhecode.browser_pilot.website.linkedin.page.games.queen.QueensGameService;
import com.achhecode.browser_pilot.website.linkedin.page.games.queen.QueensPage;
import com.achhecode.browser_pilot.website.linkedin.page.games.queen.QueensRequest;
import com.achhecode.browser_pilot.website.linkedin.page.games.tango.TangoGameService;
import com.achhecode.browser_pilot.website.linkedin.page.games.tango.TangoPage;
import com.achhecode.browser_pilot.website.linkedin.page.games.tango.TangoRequest;
import com.achhecode.browser_pilot.website.linkedin.page.games.zip.ZipGameService;
import com.achhecode.browser_pilot.website.linkedin.page.games.zip.ZipPage;
import com.achhecode.browser_pilot.website.linkedin.page.games.zip.ZipRequest;
import com.microsoft.playwright.Page;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

@Slf4j 
@Service
public class LinkedInGameService {

    private final BrowserPageResolver pageResolver;
    private final ZipGameService zipGameService;
    private final MiniSudokuGameService sudokuGameService;
    private final QueensGameService queensGameService;
    private final TangoGameService tangoGameService;

    public LinkedInGameService(
            BrowserPageResolver pageResolver,
            ZipGameService zipGameService,
            MiniSudokuGameService sudokuGameService,
            QueensGameService queensGameService,
            TangoGameService tangoGameService
    ) {
        this.pageResolver = pageResolver;
        this.zipGameService = zipGameService;
        this.sudokuGameService = sudokuGameService;
        this.queensGameService = queensGameService;
        this.tangoGameService = tangoGameService;
    }

    private MyNetworkPage getMyNetworkPage() {
        Page page = pageResolver.findPage(LinkedInUrls.HOME);

        MyNetworkPage myNetworkPage = new MyNetworkPage(page);
        myNetworkPage.open();

        return myNetworkPage;
    }

    private MyNetworkPage openGame(LinkedInGame game) {

        Page page = pageResolver.findPage(LinkedInUrls.HOME);

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

    public void enterPatches() {
        openGame(LinkedInGame.PATCHES);
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
}