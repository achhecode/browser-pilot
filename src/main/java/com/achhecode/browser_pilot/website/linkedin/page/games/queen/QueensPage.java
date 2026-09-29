package com.achhecode.browser_pilot.website.linkedin.page.games.queen;

import com.achhecode.browser_pilot.website.WebsitePage;
import com.achhecode.browser_pilot.website.linkedin.LinkedInGame;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class QueensPage extends WebsitePage {

    private static final String BOARD_SELECTOR =
            "xpath=//div[@data-testid='game-board-container']//section[@id='queens-game-board']";


    public QueensPage(Page page) {
        super(page);
    }

    public boolean isDisplayed() {
        return page.url().contains(LinkedInGame.QUEENS.path());
    }

    public void waitUntilReady() {
        page.locator(BOARD_SELECTOR)
                .waitFor(
                        new Locator.WaitForOptions()
                                .setTimeout(10_000)
                );
    }

    public void getBoardState() {
        // page.getByTestId("cell-0").click();
        page.locator("#workspace").click();
    }


    // any arrow key activates 0,0
    public void clickCellIndex(int index) {
        // page.locator(String.format("//div[@data-cell-idx='%d']", index)).click();
        page.locator(String.format("[data-cell-idx='%d']", index)).click();
    }

    public int totalCellIndex() {
        return page.locator("[data-cell-idx]").count();
    }

    public void setCellValue(int index) {

        Locator cell = page.locator(
                String.format("//div[@data-cell-idx='%d']", index)
        );

        // press two space at index
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