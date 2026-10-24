package com.achhecode.linkedinservice.page.games;

import com.achhecode.linkedinservice.LinkedInGame;
import com.achhecode.linkedinservice.handler.WebsitePage;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class PatchesPage extends WebsitePage {

    private static final String BOARD_SELECTOR = "xpath=//div[@data-testid='patches-game-container']";

    public PatchesPage(Page page) {
        super(page);
    }

    public boolean isDisplayed() {
        return page.url().contains(LinkedInGame.PATCHES.path());
    }

    public void solve() {

    }

    public void waitUntilReady() {
        page.locator(BOARD_SELECTOR).waitFor(new Locator.WaitForOptions().setTimeout(10_000));
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
