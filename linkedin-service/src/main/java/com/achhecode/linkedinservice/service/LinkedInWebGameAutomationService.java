
package com.achhecode.linkedinservice.service;

import java.util.function.Consumer;
import java.util.function.Function;

import org.springframework.stereotype.Service;

import com.achhecode.browserpilotdto.dto.MiniSudokuRequest;
import com.achhecode.browserpilotdto.dto.PatchesRequest;
import com.achhecode.browserpilotdto.dto.PinpointRequest;
import com.achhecode.browserpilotdto.dto.QueensRequest;
import com.achhecode.browserpilotdto.dto.TangoRequest;
import com.achhecode.browserpilotdto.dto.WendRequest;
import com.achhecode.browserpilotdto.dto.ZipRequest;
import com.achhecode.browserpilotdto.enums.LinkedInGame;
import com.achhecode.linkedinservice.LinkedInPageResolver;
import com.achhecode.linkedinservice.page.games.MiniSudokuPage;
import com.achhecode.linkedinservice.page.games.PatchesPage;
import com.achhecode.linkedinservice.page.games.PinpointPage;
import com.achhecode.linkedinservice.page.games.QueensPage;
import com.achhecode.linkedinservice.page.games.TangoPage;
import com.achhecode.linkedinservice.page.games.WendPage;
import com.achhecode.linkedinservice.page.games.ZipPage;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.TimeoutError;
import com.microsoft.playwright.options.WaitForSelectorState;

@Service
public class LinkedInWebGameAutomationService {

    private final LinkedInPageResolver pageResolver;

    public LinkedInWebGameAutomationService(LinkedInPageResolver pageResolver) {
        this.pageResolver = pageResolver;
    }

    private <T> void executeGame(LinkedInGame game, Function<Page, T> pageFactory, Consumer<T> action) {

        Page page = pageResolver.findPage();
        page.navigate(game.url());

        // Handle the pre-game screen if it appears.
        handleGameStartScreen(page, game);

        T gamePage = pageFactory.apply(page);
        action.accept(gamePage);
    }

    /**
     * Clicks Start game if LinkedIn displays the pre-game screen. If the user is
     * already logged in and the game is accessible, the existing flow continues
     * without clicking anything.
     */
    private void handleGameStartScreen(Page page, LinkedInGame game) {
        String startGameXPath = "//a[contains(@href, '/games/" + game.keyName() + "/')"
                + " and contains(@href, 'skipStartScreen=true')]/span";

        Locator startGameButton = page.locator(startGameXPath);

        try {
            startGameButton.first()
                    .waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(3_000));

            startGameButton.first().click();

        } catch (TimeoutError e) {
            // No pre-game screen appeared; continue with the existing flow.
        }
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

    public void solvePinpoint(PinpointRequest request) {
        executeGame(LinkedInGame.PINPOINT, PinpointPage::new, game -> game.solve(request));
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
