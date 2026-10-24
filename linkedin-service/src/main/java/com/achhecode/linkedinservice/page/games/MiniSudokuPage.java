package com.achhecode.linkedinservice.page.games;

import java.util.HashSet;
import java.util.Set;

import com.achhecode.browserpilotdto.dto.MiniSudokuRequest;
import com.achhecode.playwrightservice.handler.WebsitePage;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class MiniSudokuPage extends WebsitePage {

    private static final String BOARD_SELECTOR =
            "xpath=//section//div[contains(@class, 'sudoku-cell') "
                    + "and @data-cell-idx]";

    // private static final String CELL_SELECTOR =
            // "div.sudoku-cell[data-cell-idx]";

    private static final String PREFILLED_CELL_SELECTOR =
            ".sudoku-cell-prefilled";

    private static final String CELL_CONTENT_SELECTOR =
            ".sudoku-cell-content";

    private static final int GRID_SIZE = 6;

    private static final double DEFAULT_TIMEOUT_MS = 10_000;

    public MiniSudokuPage(Page page) {
        super(page);
    }

    @Override
    public boolean isDisplayed() {
        return page.url().contains("/games/mini-sudoku/");
    }

    /**
     * Wait until the Sudoku board and its cells are ready.
     */
    public void waitUntilReady() {
        page.locator(BOARD_SELECTOR)
                .first()
                .waitFor(
                        new Locator.WaitForOptions()
                                .setTimeout(DEFAULT_TIMEOUT_MS)
                );
    }

    /**
     * Returns the total number of cells currently rendered.
     */
    public int totalCellIndex() {
        return page.locator(BOARD_SELECTOR).count();
    }

    /**
     * Fills all cells from the supplied solution.
     *
     * Coordinates are 1-based.
     * Prefilled cells are skipped.
     */
    public void solve(MiniSudokuRequest request) {
        if (request == null || request.cells() == null) {
            throw new IllegalArgumentException(
                    "MiniSudokuRequest and its cells must not be null"
            );
        }

        waitUntilReady();

        int totalCells = totalCellIndex();

        if (totalCells != GRID_SIZE * GRID_SIZE) {
            throw new IllegalStateException(
                    "Expected a " + GRID_SIZE + "x" + GRID_SIZE
                            + " Sudoku grid, but found "
                            + totalCells + " cells."
            );
        }

        Set<Integer> processedCells = new HashSet<>();

        for (MiniSudokuRequest.Cell answer : request.cells()) {
            if (answer == null || answer.coordinates() == null) {
                throw new IllegalArgumentException(
                        "Cell answer and coordinates must not be null"
                );
            }

            int row = answer.coordinates().row();
            int column = answer.coordinates().column();
            int value = answer.value();

            validateCoordinates(row, column);
            validateValue(value);

            int cellIndex = getCellIndex(row, column);

            if (!processedCells.add(cellIndex)) {
                throw new IllegalArgumentException(
                        "Duplicate cell in request: row=" + row
                                + ", column=" + column
                );
            }

            fillCell(row, column, value);
        }
    }

    /**
     * Interacts with a cell using 1-based coordinates.
     *
     * Clicking selects the cell; the value is then entered
     * using the keyboard.
     */
    public void clickCell(int row, int column) {
        validateCoordinates(row, column);

        int cellIndex = getCellIndex(row, column);

        Locator cell = getCell(cellIndex);

        cell.waitFor(
                new Locator.WaitForOptions()
                        .setTimeout(DEFAULT_TIMEOUT_MS)
        );

        if (isPrefilled(cell)) {
            return;
        }

        cell.click();
    }

    /**
     * Enters the requested number into an editable cell.
     */
    public void fillCell(int row, int column, int value) {
        validateCoordinates(row, column);
        validateValue(value);

        int cellIndex = getCellIndex(row, column);
        Locator cell = getCell(cellIndex);

        cell.waitFor(
                new Locator.WaitForOptions()
                        .setTimeout(DEFAULT_TIMEOUT_MS)
        );

        // Never overwrite a prefilled clue.
        if (isPrefilled(cell)) {
            return;
        }

        String currentValue = cell.locator(CELL_CONTENT_SELECTOR)
                .innerText()
                .trim();

        // Avoid unnecessary interaction if the cell already has
        // the expected answer.
        if (String.valueOf(value).equals(currentValue)) {
            return;
        }

        cell.click();

        // The game accepts numbers through keyboard input.
        page.keyboard().press(String.valueOf(value));
    }

    private Locator getCell(int cellIndex) {
        return page.locator(BOARD_SELECTOR)
                .nth(cellIndex);
    }

    private boolean isPrefilled(Locator cell) {
        return cell.getAttribute("class") != null
                && cell.getAttribute("class")
                        .contains(PREFILLED_CELL_SELECTOR);
    }

    /**
     * Maps 1-based coordinates to zero-based data-cell-idx.
     *
     * Example:
     * (1,1) -> 0
     * (1,6) -> 5
     * (2,1) -> 6
     * (6,6) -> 35
     */
    private int getCellIndex(int row, int column) {
        return (row - 1) * GRID_SIZE + (column - 1);
    }

    private void validateCoordinates(int row, int column) {
        if (row < 1 || row > GRID_SIZE
                || column < 1 || column > GRID_SIZE) {
            throw new IllegalArgumentException(
                    "Invalid Sudoku coordinates: row=" + row
                            + ", column=" + column
                            + ". Valid range is 1-" + GRID_SIZE + "."
            );
        }
    }

    private void validateValue(int value) {
        if (value < 1 || value > GRID_SIZE) {
            throw new IllegalArgumentException(
                    "Invalid Sudoku value: " + value
                            + ". Valid values are 1-" + GRID_SIZE + "."
            );
        }
    }
}
