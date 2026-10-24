
package com.achhecode.linkedinservice.page.games;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.achhecode.browserpilotdto.dto.Coordinates;
import com.achhecode.linkedinservice.LinkedInGame;
import com.achhecode.playwrightservice.handler.WebsitePage;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class QueensPage extends WebsitePage {

    private static final Logger LOGGER =
            Logger.getLogger(QueensPage.class.getName());

    private static final String BOARD_SELECTOR =
            "xpath=//div[@data-testid='game-board-container']"
            + "//section[@id='queens-game-board']";

    private static final String CELL_SELECTOR =
            BOARD_SELECTOR + "//*[@data-cell-idx][@role='button']";

    private static final Pattern CELL_LABEL_PATTERN =
            Pattern.compile(
                    ".*row\\s+(\\d+),\\s*column\\s+(\\d+).*",
                    Pattern.CASE_INSENSITIVE
            );

    public QueensPage(Page page) {
        super(page);
    }

    public boolean isDisplayed() {
        return page.url().contains(LinkedInGame.QUEENS.path());
    }

    public void waitUntilReady() {
        page.locator(BOARD_SELECTOR).waitFor(
                new Locator.WaitForOptions().setTimeout(10_000)
        );
    }

    public int totalCellIndex() {
        return page.locator(CELL_SELECTOR).count();
    }

    /**
     * Applies a Queens solution.
     *
     * Answer coordinates are 1-based.
     * Each answer cell is clicked twice to place a queen.
     * Other cells in the same row are clicked once to place crosses.
     */
    public void solve(List<Coordinates> answer) {
        if (answer == null || answer.isEmpty()) {
            LOGGER.warning("Queens answer is empty; nothing to solve.");
            return;
        }

        waitUntilReady();

        int totalCells = totalCellIndex();
        int gridSize = (int) Math.sqrt(totalCells);

        if (totalCells == 0 || gridSize * gridSize != totalCells) {
            LOGGER.warning(
                    "Queens board does not appear to be square. Cell count: "
                            + totalCells
            );
            return;
        }

        Set<Coordinates> queenCoordinates = new HashSet<>(answer);

        if (queenCoordinates.size() != answer.size()) {
            LOGGER.warning("Queens answer contains duplicate coordinates.");
        }

        // Check that every target is within the board.
        for (Coordinates queen : queenCoordinates) {
            if (queen.row() < 1 || queen.row() > gridSize
                    || queen.column() < 1 || queen.column() > gridSize) {
                LOGGER.warning("Queen coordinate is outside the board: " + queen);
            }
        }

        for (int row = 1; row <= gridSize; row++) {
            for (int column = 1; column <= gridSize; column++) {
                Coordinates coordinates = new Coordinates(row, column);

                if (row < 1 || row > gridSize
                        || column < 1 || column > gridSize) {
                    continue;
                }

                int cellIndex = (row - 1) * gridSize + (column - 1);

                if (cellIndex >= totalCells) {
                    LOGGER.warning(
                            "Cell index out of range for " + coordinates
                    );
                    continue;
                }

                Locator cell = page.locator(CELL_SELECTOR)
                        .filter(new Locator.FilterOptions()
                                .setHas(page.locator(
                                        "[data-cell-idx='" + cellIndex + "']"
                                )));

                // Directly select the cell by its index.
                cell = page.locator(
                        BOARD_SELECTOR
                        + "//*[@data-cell-idx='" + cellIndex
                        + "' and @role='button']"
                );

                if (cell.count() == 0) {
                    LOGGER.warning(
                            "No cell found for " + coordinates
                                    + " (data-cell-idx=" + cellIndex + ")"
                    );
                    continue;
                }

                Locator target = cell.first();

                validateCellLabel(target, row, column);

                String ariaDisabled = target.getAttribute("aria-disabled");

                if ("true".equalsIgnoreCase(ariaDisabled)) {
                    // It may already be a queen or otherwise disabled.
                    continue;
                }

                if (queenCoordinates.contains(coordinates)) {
                    putQueen(target, coordinates);
                } else {
                    putCross(target, coordinates);
                }
            }
        }
    }

    private void validateCellLabel(
            Locator cell,
            int expectedRow,
            int expectedColumn
    ) {
        String ariaLabel = cell.getAttribute("aria-label");

        if (ariaLabel == null) {
            LOGGER.warning(
                    "Cell has no aria-label at expected row "
                            + expectedRow + ", column " + expectedColumn
            );
            return;
        }

        Matcher matcher = CELL_LABEL_PATTERN.matcher(ariaLabel);

        if (!matcher.matches()) {
            LOGGER.warning(
                    "Could not parse cell coordinates from aria-label: "
                            + ariaLabel
            );
            return;
        }

        int actualRow = Integer.parseInt(matcher.group(1));
        int actualColumn = Integer.parseInt(matcher.group(2));

        if (actualRow != expectedRow || actualColumn != expectedColumn) {
            LOGGER.warning(
                    "Cell coordinate mismatch. Expected row "
                            + expectedRow + ", column " + expectedColumn
                            + " but aria-label says row " + actualRow
                            + ", column " + actualColumn
                            + ". Continuing."
            );
        }
    }

    private void putCross(Locator cell, Coordinates coordinates) {
        if ("true".equalsIgnoreCase(cell.getAttribute("aria-disabled"))) {
            return;
        }

        cell.click();
    }

    private void putQueen(Locator cell, Coordinates coordinates) {
        if ("true".equalsIgnoreCase(cell.getAttribute("aria-disabled"))) {
            return;
        }

        cell.click();

        // The first click changes the cell state; the second places the queen.
        if (!"true".equalsIgnoreCase(cell.getAttribute("aria-disabled"))) {
            cell.click();
        }
    }

    public void putCross(int row, int column) {
        clickCell(row, column, false);
    }

    public void putQueen(int row, int column) {
        clickCell(row, column, true);
    }

    private void clickCell(int row, int column, boolean queen) {
        int totalCells = totalCellIndex();
        int gridSize = (int) Math.sqrt(totalCells);

        if (row < 1 || row > gridSize
                || column < 1 || column > gridSize) {
            LOGGER.warning(
                    "Invalid cell coordinates: row " + row
                            + ", column " + column
            );
            return;
        }

        int cellIndex = (row - 1) * gridSize + (column - 1);

        Locator cell = page.locator(
                BOARD_SELECTOR
                + "//*[@data-cell-idx='" + cellIndex
                + "' and @role='button']"
        );

        if (cell.count() == 0) {
            LOGGER.warning("Cell not found: row " + row + ", column " + column);
            return;
        }

        Locator target = cell.first();
        validateCellLabel(target, row, column);

        if ("true".equalsIgnoreCase(target.getAttribute("aria-disabled"))) {
            return;
        }

        target.click();

        if (queen
                && !"true".equalsIgnoreCase(
                        target.getAttribute("aria-disabled"))) {
            target.click();
        }
    }

    public void reset() {
        Locator resetButton = page.getByRole(
                com.microsoft.playwright.options.AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName(
                        Pattern.compile("reset", Pattern.CASE_INSENSITIVE)
                )
        );

        if (resetButton.count() > 0) {
            resetButton.first().click();
        } else {
            LOGGER.warning("Could not find the Queens reset button.");
        }
    }

    public void submit() {
        Locator submitButton = page.getByRole(
                com.microsoft.playwright.options.AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName(
                        Pattern.compile(
                                "check|submit|submit answer",
                                Pattern.CASE_INSENSITIVE
                        )
                )
        );

        if (submitButton.count() > 0) {
            submitButton.first().click();
        } else {
            LOGGER.warning("Could not find the Queens submit/check button.");
        }
    }
}
