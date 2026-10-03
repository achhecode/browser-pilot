package com.achhecode.browser_pilot.website.linkedin.page.games.pinpoint;


import com.achhecode.browser_pilot.website.WebsitePage;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class PinpointPage extends WebsitePage {

    private static final String BOARD_SELECTOR =
            "xpath=//div[contains(@class, 'pinpoint__wrapper') and contains(@class, 'game-board')]";

    public PinpointPage(Page page) {
        super(page);
    }

    public boolean isDisplayed() {
        return page.url().contains("/games/pinpoint/");
    }

    public void waitUntilReady() {
        page.locator(BOARD_SELECTOR)
                .waitFor(
                        new Locator.WaitForOptions()
                                .setTimeout(10_000)
                );
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