
package com.achhecode.linkedinservice.service;

import java.util.function.Consumer;
import java.util.function.Function;

import org.springframework.stereotype.Service;

import com.achhecode.browserpilotdto.dto.MiniSudokuRequest;
import com.achhecode.browserpilotdto.dto.PatchesRequest;
import com.achhecode.browserpilotdto.dto.QueensRequest;
import com.achhecode.browserpilotdto.dto.TangoRequest;
import com.achhecode.browserpilotdto.dto.WendRequest;
import com.achhecode.browserpilotdto.dto.ZipRequest;
import com.achhecode.linkedinservice.LinkedInGame;
import com.achhecode.linkedinservice.LinkedInPageResolver;
import com.achhecode.linkedinservice.page.games.MiniSudokuPage;
import com.achhecode.linkedinservice.page.games.PatchesPage;
import com.achhecode.linkedinservice.page.games.PinpointPage;
import com.achhecode.linkedinservice.page.games.QueensPage;
import com.achhecode.linkedinservice.page.games.TangoPage;
import com.achhecode.linkedinservice.page.games.WendPage;
import com.achhecode.linkedinservice.page.games.ZipPage;
import com.microsoft.playwright.Page;

@Service
public class LinkedInWebGameAutomationService {

    private final LinkedInPageResolver pageResolver;

    public LinkedInWebGameAutomationService(LinkedInPageResolver pageResolver) {
        this.pageResolver = pageResolver;
    }

    private <T> void executeGame(LinkedInGame game, Function<Page, T> pageFactory, Consumer<T> action) {

        Page page = pageResolver.findPage();
        page.navigate(game.url());

        T gamePage = pageFactory.apply(page);
        action.accept(gamePage);
    }

    public void solveZip(ZipRequest request) {
        executeGame(LinkedInGame.ZIP, ZipPage::new, game -> game.solve(request));
    }

    public void solveQueens(QueensRequest request) {
        executeGame(LinkedInGame.QUEENS, QueensPage::new, game -> game.solve(request.request()));
    }

    public void solveMiniSudoku(MiniSudokuRequest request) {
        executeGame(LinkedInGame.MINI_SUDOKU, MiniSudokuPage::new, game -> game.solve(request));
    }

    public void solvePinpoint() {
        executeGame(LinkedInGame.PINPOINT, PinpointPage::new, game -> game.solve());
    }

    public void solveTango(TangoRequest request) {
        executeGame(LinkedInGame.TANGO, TangoPage::new, game -> game.solve(request));
    }

    public void solveWend(WendRequest request) {
        executeGame(LinkedInGame.WEND, WendPage::new, game -> game.solve(request.request()));
    }

    public void solvePatches(PatchesRequest request) {
        executeGame(LinkedInGame.PATCHES, PatchesPage::new, game -> game.solve(request.request()));
    }
}
