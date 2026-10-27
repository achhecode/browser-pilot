
package com.achhecode.linkedinservice.page.games;

import com.achhecode.browserpilotdto.dto.PinpointRequest;
import com.achhecode.linkedinservice.handler.WebsitePage;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;

public class PinpointPage extends WebsitePage {

    private static final String BOARD_SELECTOR = "xpath=//div[contains(@class, 'game-board')]";

    private static final String GUESS_INPUT_SELECTOR = "xpath=//input[@class='pinpoint__input']";

    private static final String GUESS_BUTTON_SELECTOR = "xpath=//button[contains(@class, 'pinpoint__submit-btn')]";

    private static final int DEFAULT_TIMEOUT_MS = 5_000;

    public PinpointPage(Page page) {
        super(page);
    }

    @Override
    public boolean isDisplayed() {
        return page.url().contains("/games/pinpoint/");
    }

    /**
     * Checks whether the game board is visible and ready.
     */
    public boolean isBoardReady() {
        Locator board = page.locator(BOARD_SELECTOR);

        try {
            board.waitFor(
                    new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(DEFAULT_TIMEOUT_MS));

            return true;
        } catch (com.microsoft.playwright.TimeoutError e) {
            return false;
        }
    }

    /**
     * Submits a guess using the Guess button.
     */
    public void solve(PinpointRequest request) {
        if (request == null || request.request() == null || request.request().isBlank()) {
            throw new IllegalArgumentException("Pinpoint guess must not be empty.");
        }

        if (!isBoardReady()) {
            throw new IllegalStateException("Pinpoint game board is not ready.");
        }

        if (gameSolved())
            return;

        Locator guessInput = page.locator(GUESS_INPUT_SELECTOR);

        guessInput.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));

        guessInput.fill(request.request());

        Locator guessButton = page.locator(GUESS_BUTTON_SELECTOR);

        guessButton.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));

        guessButton.click();
    }

    /**
     * Submits a guess by pressing Enter.
     */
    public void solveByPressingEnter(PinpointRequest request) {
        if (request == null || request.request() == null || request.request().isBlank()) {
            throw new IllegalArgumentException("Pinpoint guess must not be empty.");
        }

        if (!isBoardReady()) {
            throw new IllegalStateException("Pinpoint game board is not ready.");
        }

        Locator guessInput = page.locator(GUESS_INPUT_SELECTOR);

        guessInput.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));

        guessInput.fill(request.request());
        guessInput.press("Enter");
    }

    private boolean gameSolved() {
        return page.locator("//button[contains(normalize-space(.), 'See results')]").count() > 0;
    }
}
