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
import com.microsoft.playwright.PlaywrightException;
import com.microsoft.playwright.TimeoutError;
import com.microsoft.playwright.options.WaitForSelectorState;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class LinkedInWebGameAutomationService {

    private static final String MINI_SUDOKU_START_BUTTON = "button#launch-footer-start-button";

    private static final int MINI_SUDOKU_BUTTON_TIMEOUT_MS = 1_000;
    private static final int OTHER_GAME_START_TIMEOUT_MS = 3_000;

    private final LinkedInPageResolver pageResolver;

    public LinkedInWebGameAutomationService(LinkedInPageResolver pageResolver) {
        this.pageResolver = pageResolver;
    }

    private <T> void executeGame(LinkedInGame game, Function<Page, T> pageFactory, Consumer<T> action) {

        Page page = pageResolver.findPage();

        page.navigate(game.url());

        handleGameStartScreen(page, game);

        T gamePage = pageFactory.apply(page);
        action.accept(gamePage);
    }

    /**
     * Handles an optional pre-game screen.
     *
     * Mini Sudoku may open directly when the user is logged in. Other games retain
     * the existing start-screen behavior.
     */
    private void handleGameStartScreen(Page page, LinkedInGame game) {

        if (game == LinkedInGame.MINI_SUDOKU) {
            clickMiniSudokuStartButton(page);
            return;
        }

        String startGameXPath = "//a[contains(@href, '/games/" + game.keyName()
                + "/') and contains(@href, 'skipStartScreen=true')]/span";

        Locator startGameButton = page.locator(startGameXPath).first();

        try {
            startGameButton.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE)
                    .setTimeout(OTHER_GAME_START_TIMEOUT_MS));

            startGameButton.click();

        } catch (TimeoutError e) {
            // No pre-game screen appeared; continue with the existing flow.
        }
    }

    /**
     * Attempts to click the Mini Sudoku start button if it is present.
     *
     * The button may be absent for logged-in users. LinkedIn may also navigate or
     * replace the document after the button is clicked. Neither situation should
     * fail this optional step.
     */
    private void clickMiniSudokuStartButton(Page page) {

        try {
            Locator button = page.locator(MINI_SUDOKU_START_BUTTON).first();

            // Wait briefly for the button to appear.
            button.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE)
                    .setTimeout(MINI_SUDOKU_BUTTON_TIMEOUT_MS));

            // Do not use deprecated IsVisibleOptions or setNoWaitAfter.
            button.click(new Locator.ClickOptions().setTimeout(2_000));

            log.debug("Clicked the Mini Sudoku start button.");

        } catch (TimeoutError e) {
            // Expected when the button is absent for logged-in users.
            log.debug("Mini Sudoku start button is not displayed; continuing.");
        } catch (PlaywrightException e) {
            // The document or frame may have navigated while clicking.
            // The actual board readiness check will determine whether
            // the game is available.
            log.debug("Mini Sudoku start button could not be clicked; " + "continuing to board detection. Reason: {}",
                    e.getMessage());
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