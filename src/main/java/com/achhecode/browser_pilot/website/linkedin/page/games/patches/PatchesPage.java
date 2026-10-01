package com.achhecode.browser_pilot.website.linkedin.page.games.patches;

import com.achhecode.browser_pilot.website.WebsitePage;
import com.achhecode.browser_pilot.website.linkedin.LinkedInGame;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class PatchesPage extends WebsitePage {

    private static final String BOARD_SELECTOR =
            "xpath=//div[@data-testid='patches-game-container']";

    public PatchesPage(Page page) {
        super(page);
    }

    public boolean isDisplayed() {
        return page.url().contains(LinkedInGame.PATCHES.path());
    }

    public void waitUntilReady() {
        page.locator(BOARD_SELECTOR)
                .waitFor(
                        new Locator.WaitForOptions()
                                .setTimeout(10_000)
                );
    }

    public int totalCellIndex() {
        return page.locator("[data-cell-idx]").count();
    }

    public String getBoardState() {
        // Read board from DOM here.
        return "";
    }

    public void clickCell(int row, int column) {
        // Playwright interaction with the board.
    }

    public void reset() {
        // Click reset button.
    }

    public void submit() {
        // Click submit/check button.
    }
    
}
