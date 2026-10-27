package com.achhecode.linkedinservice.page.games;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.achhecode.browserpilotdto.dto.Coordinates;
import com.achhecode.browserpilotdto.enums.LinkedInGame;
import com.achhecode.linkedinservice.handler.WebsitePage;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

public class QueensPage extends WebsitePage {

    private static final Logger LOGGER = Logger.getLogger(QueensPage.class.getName());

    private static final String BOARD_SELECTOR = "xpath=//div[@data-testid='game-board-container']"
            + "//section[@id='queens-game-board']";

    private static final String CELL_SELECTOR = BOARD_SELECTOR + "//*[@data-cell-idx][@role='button']";

    private static final Pattern CELL_LABEL_PATTERN = Pattern.compile(".*row\\s+(\\d+),\\s*column\\s+(\\d+).*",
            Pattern.CASE_INSENSITIVE);

    private static final double DEFAULT_TIMEOUT_MS = 10_000;

    /** Minimum solve time (2 s) plus a margin for network/render latency. */
    private static final long MIN_SOLVE_MS = 2_000;
    private static final long SAFETY_MARGIN_MS = 150;

    /** One paced action: a single click (cross) or two clicks (queen). */
    private record Step(int row, int column, int cellIndex, boolean queen) {
    }

    public QueensPage(Page page) {
        super(page);
    }

    @Override
    public boolean isDisplayed() {
        return page.url().contains(LinkedInGame.QUEENS.path());
    }

    public void waitUntilReady() {
        page.locator(BOARD_SELECTOR).waitFor(new Locator.WaitForOptions().setTimeout(DEFAULT_TIMEOUT_MS));
    }

    public int totalCellIndex() {
        return page.locator(CELL_SELECTOR).count();
    }

    /**
     * Applies a Queens solution (1-based coordinates).
     * <p>
     * Each answer cell is clicked twice to place a queen; every other cell is
     * clicked once to place a cross. Phases:
     * <ol>
     * <li>validate the answer against the board (no clicks yet),</li>
     * <li>build the step list, skipping disabled cells, with crosses first and
     * queens last so the final queen is the action that completes the board,</li>
     * <li>run the steps paced by {@link MinDurationPacer}, so completion lands no
     * earlier than {@link #MIN_SOLVE_MS} (plus margin) after input started.</li>
     * </ol>
     */
    public void solve(List<Coordinates> answer) {
        if (answer == null || answer.isEmpty()) {
            LOGGER.warning("Queens answer is empty; nothing to solve.");
            return;
        }

        waitUntilReady();

        int gridSize = gridSize();

        // Phase 1: fail fast, before touching the game.
        Set<Coordinates> queens = validate(answer, gridSize);

        // Phase 2: decide what to click. DOM reads happen here, not in the timed loop.
        List<Step> crosses = new ArrayList<>();
        List<Step> queenSteps = new ArrayList<>();

        for (int row = 1; row <= gridSize; row++) {
            for (int column = 1; column <= gridSize; column++) {
                int cellIndex = toCellIndex(row, column, gridSize);
                Locator cell = getCell(cellIndex);

                if (cell.count() == 0) {
                    LOGGER.warning("No cell found for row " + row + ", column " + column + " (data-cell-idx="
                            + cellIndex + ")");
                    continue;
                }

                validateCellLabel(cell, row, column);

                if (isDisabled(cell)) {
                    // Already a queen or otherwise locked.
                    continue;
                }

                boolean queen = queens.contains(new Coordinates(row, column));
                (queen ? queenSteps : crosses).add(new Step(row, column, cellIndex, queen));
            }
        }

        List<Step> steps = new ArrayList<>(crosses.size() + queenSteps.size());
        steps.addAll(crosses);
        steps.addAll(queenSteps); // queens last
        if (steps.isEmpty()) {
            return;
        }

        // Phase 3: paced input, clicks only.
        MinDurationPacer pacer = MinDurationPacer.start(page, MIN_SOLVE_MS + SAFETY_MARGIN_MS, steps.size());

        for (int i = 0; i < steps.size(); i++) {
            Step step = steps.get(i);
            pacer.awaitStep(i);
            click(getCell(step.cellIndex()), step.queen());
        }
    }

    public void putCross(int row, int column) {
        clickCell(row, column, false);
    }

    public void putQueen(int row, int column) {
        clickCell(row, column, true);
    }

    public void reset() {
        Locator resetButton = page.getByRole(AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName(Pattern.compile("reset", Pattern.CASE_INSENSITIVE)));

        if (resetButton.count() > 0) {
            resetButton.first().click();
        } else {
            LOGGER.warning("Could not find the Queens reset button.");
        }
    }

    public void submit() {
        Locator submitButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions()
                .setName(Pattern.compile("check|submit|submit answer", Pattern.CASE_INSENSITIVE)));

        if (submitButton.count() > 0) {
            submitButton.first().click();
        } else {
            LOGGER.warning("Could not find the Queens submit/check button.");
        }
    }

    /** Unpaced single-cell action (1-based coordinates). */
    private void clickCell(int row, int column, boolean queen) {
        int gridSize = gridSize();

        if (row < 1 || row > gridSize || column < 1 || column > gridSize) {
            LOGGER.warning("Invalid cell coordinates: row " + row + ", column " + column);
            return;
        }

        Locator cell = getCell(toCellIndex(row, column, gridSize));

        if (cell.count() == 0) {
            LOGGER.warning("Cell not found: row " + row + ", column " + column);
            return;
        }

        validateCellLabel(cell, row, column);

        if (isDisabled(cell)) {
            return;
        }

        click(cell, queen);
    }

    /** One click places a cross; a second click turns it into a queen. */
    private void click(Locator cell, boolean queen) {
        cell.click();
        if (queen) {
            cell.click();
        }
    }

    /**
     * Checks the answer against the board and returns it as a set.
     */
    private Set<Coordinates> validate(List<Coordinates> answer, int gridSize) {
        Set<Coordinates> queens = new HashSet<>();

        for (Coordinates queen : answer) {
            if (queen == null) {
                throw new IllegalArgumentException("Queens answer contains a null coordinate");
            }
            if (queen.row() < 1 || queen.row() > gridSize || queen.column() < 1 || queen.column() > gridSize) {
                throw new IllegalArgumentException(
                        "Queen coordinate is outside the " + gridSize + "x" + gridSize + " board: " + queen);
            }
            if (!queens.add(queen)) {
                throw new IllegalArgumentException("Queens answer contains duplicate coordinates: " + queen);
            }
        }
        return queens;
    }

    /** Board side length; throws if the rendered cells don't form a square. */
    private int gridSize() {
        int totalCells = totalCellIndex();
        int gridSize = (int) Math.round(Math.sqrt(totalCells));

        if (totalCells == 0 || gridSize * gridSize != totalCells) {
            throw new IllegalStateException("Queens board does not appear to be square. Cell count: " + totalCells);
        }
        return gridSize;
    }

    private Locator getCell(int cellIndex) {
        return page.locator(BOARD_SELECTOR + "//*[@data-cell-idx='" + cellIndex + "' and @role='button']").first();
    }

    private boolean isDisabled(Locator cell) {
        return "true".equalsIgnoreCase(cell.getAttribute("aria-disabled"));
    }

    private int toCellIndex(int row, int column, int gridSize) {
        return (row - 1) * gridSize + (column - 1);
    }

    private void validateCellLabel(Locator cell, int expectedRow, int expectedColumn) {
        String ariaLabel = cell.getAttribute("aria-label");

        if (ariaLabel == null) {
            LOGGER.warning("Cell has no aria-label at expected row " + expectedRow + ", column " + expectedColumn);
            return;
        }

        Matcher matcher = CELL_LABEL_PATTERN.matcher(ariaLabel);

        if (!matcher.matches()) {
            LOGGER.warning("Could not parse cell coordinates from aria-label: " + ariaLabel);
            return;
        }

        int actualRow = Integer.parseInt(matcher.group(1));
        int actualColumn = Integer.parseInt(matcher.group(2));

        if (actualRow != expectedRow || actualColumn != expectedColumn) {
            LOGGER.warning("Cell coordinate mismatch. Expected row " + expectedRow + ", column " + expectedColumn
                    + " but aria-label says row " + actualRow + ", column " + actualColumn + ". Continuing.");
        }
    }
}