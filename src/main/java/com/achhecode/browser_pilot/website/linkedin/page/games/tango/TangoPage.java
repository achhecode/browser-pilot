
package com.achhecode.browser_pilot.website.linkedin.page.games.tango;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import com.achhecode.browser_pilot.grid.Coordinates;
import com.achhecode.browser_pilot.website.WebsitePage;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class TangoPage extends WebsitePage {

    private static final String BOARD_SELECTOR = "xpath=//div[@data-testid='tango-game-container']";

    private static final String CELL_SELECTOR = "[data-cell-idx][role='button']";

    private static final int GRID_SIZE = 6;

    private static final double DEFAULT_TIMEOUT_MS = 10_000;

    public TangoPage(Page page) {
        super(page);
    }

    @Override
    public boolean isDisplayed() {
        return page.url().contains("/games/tango/");
    }

    /**
     * Wait until the Tango board and its cells are ready.
     */
    public void waitUntilReady() {
        page.locator(BOARD_SELECTOR).waitFor(new Locator.WaitForOptions().setTimeout(DEFAULT_TIMEOUT_MS));

        page.locator(BOARD_SELECTOR).locator(CELL_SELECTOR).first()
                .waitFor(new Locator.WaitForOptions().setTimeout(DEFAULT_TIMEOUT_MS));
    }

    /**
     * Returns the number of cells currently rendered on the board.
     */
    public int totalCellIndex() {
        return page.locator(BOARD_SELECTOR).locator(CELL_SELECTOR).count();
    }

    /**
     * Applies all answers from the supplied TangoRequest.
     *
     * SUN -> click once MOON -> click twice
     *
     * Already-disabled cells are skipped.
     */
    public void solve(TangoRequest request) {
        if (request == null || request.answer() == null) {
            throw new IllegalArgumentException("TangoRequest and its answer list must not be null");
        }

        waitUntilReady();

        Set<Integer> processedCells = new HashSet<>();

        for (TangoRequest.Answer answer : request.answer()) {
            if (answer == null || answer.coordinates() == null) {
                throw new IllegalArgumentException("Answer and coordinates must not be null");
            }

            Coordinates coordinates = answer.coordinates();

            int row = coordinates.row();
            int column = coordinates.column();

            validateCoordinates(row, column);

            int cellIndex = getCellIndex(row, column);

            if (!processedCells.add(cellIndex)) {
                throw new IllegalArgumentException("Duplicate cell in request: row=" + row + ", column=" + column);
            }

            putValue(answer.value(), row, column);
        }
    }

    /**
     * Clicks the cell once to place a Sun.
     */
    public void putSun(int row, int column) {
        clickCell(row, column, 1);
    }

    /**
     * Clicks the cell twice to place a Moon.
     *
     * If the first click changes the cell state, the second click is performed only
     * if the cell remains enabled.
     */
    public void putMoon(int row, int column) {
        clickCell(row, column, 2);
    }

    private void putValue(String value, int row, int column) {
        if (value == null) {
            throw new IllegalArgumentException("Cell value must not be null");
        }

        switch (value.trim().toUpperCase(Locale.ROOT)) {
        case "SUN" -> putSun(row, column);
        case "MOON" -> putMoon(row, column);
        default -> throw new IllegalArgumentException("Unsupported Tango value: " + value + ". Expected SUN or MOON.");
        }
    }

    private void clickCell(int row, int column, int clickCount) {
        validateCoordinates(row, column);

        int cellIndex = getCellIndex(row, column);

        Locator cell = page.locator(BOARD_SELECTOR).locator("[data-cell-idx='" + cellIndex + "'][role='button']");

        cell.waitFor(new Locator.WaitForOptions().setTimeout(DEFAULT_TIMEOUT_MS));

        for (int click = 0; click < clickCount; click++) {
            String disabled = cell.getAttribute("aria-disabled");

            if ("true".equalsIgnoreCase(disabled)) {
                return;
            }

            cell.click();

            // Allow the application to update the cell state.
            // Re-query the cell before the next click.
            cell = page.locator(BOARD_SELECTOR).locator("[data-cell-idx='" + cellIndex + "'][role='button']");
        }
    }

    private void validateCoordinates(int row, int column) {
        if (row < 1 || row > GRID_SIZE || column < 1 || column > GRID_SIZE) {
            throw new IllegalArgumentException("Invalid Tango coordinates: row=" + row + ", column=" + column
                    + ". Valid range is 1-" + GRID_SIZE + ".");
        }
    }

    /**
     * Maps 1-based grid coordinates to the DOM data-cell-idx.
     *
     * IMPORTANT: Replace this mapping with the exact mapping observed in the
     * current Tango board. The two supplied examples alone do not establish the
     * complete mapping for all 36 cells.
     */
    private int getCellIndex(int row, int column) {
        return (row - 1) * GRID_SIZE + (column - 1);
    }
}
