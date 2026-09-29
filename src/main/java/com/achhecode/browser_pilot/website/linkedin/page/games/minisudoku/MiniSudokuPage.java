package com.achhecode.browser_pilot.website.linkedin.page.games.minisudoku;

import com.achhecode.browser_pilot.website.WebsitePage;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class MiniSudokuPage extends WebsitePage {

    private static final String BOARD_SELECTOR =
            // "xpath=//section[contains(@class, 'sudoku-board') and @data-sudoku-grid='true']";
            "xpath=//section[contains(@class, 'sudoku-board') and @data-sudoku-grid='true']//div[contains(@class, 'sudoku-grid') and contains(@class, 'grid-game-board')]";
    
    public MiniSudokuPage(Page page) {
        super(page);
    }

    public boolean isDisplayed() {
        return page.url().contains("/games/mini-sudoku/");
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

    // any arrow key activates 0,0
    public void clickCellIndex(int index) {
        // page.locator(String.format("//div[@data-cell-idx='%d']", index)).click();
        page.locator(String.format("[data-cell-idx='%d']", index)).click();
    }

    public int totalCellIndex() {
        return page.locator("[data-cell-idx]").count();
    }

    public void setCellValue(int index, char value) {

        Locator cell = page.locator(
                String.format("//div[@data-cell-idx='%d']", index)
        );

        // Do not modify prefilled cells
        if (cell.locator(".sudoku-cell-prefilled").count() > 0) {
            return;
        }

        cell.locator(".sudoku-cell-content")
                .evaluate(
                        "(el, value) => el.textContent = value",
                        String.valueOf(value)
                );
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