package com.achhecode.linkedinservice.page.games;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.achhecode.browserpilotdto.dto.Cell;
import com.achhecode.browserpilotdto.dto.MiniSudokuRequest;
import com.achhecode.linkedinservice.handler.WebsitePage;
import com.microsoft.playwright.Frame;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class MiniSudokuPage extends WebsitePage {

    private Frame gameFrame;

    private static final String BOARD_SELECTOR = "xpath=//section//div[contains(@class, 'sudoku-cell') "
            + "and @data-cell-idx]";

    /** Class name (not a CSS selector, so no leading dot). */
    private static final String PREFILLED_CLASS = "sudoku-cell-prefilled";

    private static final String CELL_CONTENT_SELECTOR = ".sudoku-cell-content";

    private static final int GRID_SIZE = 6;

    private static final long DEFAULT_TIMEOUT_MS = 5_000L;

    /** Minimum solve time (2 s) plus a margin for network/render latency. */
    private static final long MIN_SOLVE_MS = 2_000;
    private static final long SAFETY_MARGIN_MS = 150;

    /** A validated cell write: zero-based cell index and the digit to enter. */
    private record PendingFill(int cellIndex, int value) {
    }

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
        long deadline = System.nanoTime() + java.util.concurrent.TimeUnit.MILLISECONDS.toNanos(DEFAULT_TIMEOUT_MS);

        while (System.nanoTime() < deadline) {
            for (Frame frame : page.frames()) {
                Locator cells = frame.locator(BOARD_SELECTOR);

                if (cells.count() == GRID_SIZE * GRID_SIZE && cells.first().isVisible()) {
                    gameFrame = frame;
                    return;
                }
            }

            page.waitForTimeout(200);
        }

        throw new IllegalStateException("Sudoku board not ready in any frame. URL: " + page.url());
    }

    /**
     * Returns the total number of cells currently rendered.
     */
    public int totalCellIndex() {
        return boardLocator().count();
    }

    /**
     * Fills all editable cells from the supplied solution.
     * <p>
     * Coordinates are 1-based. Prefilled cells and cells that already hold the
     * right value are skipped. Steps run in three phases:
     * <ol>
     * <li>validate the whole request (no page interaction),</li>
     * <li>work out which cells actually need input,</li>
     * <li>enter those cells, paced by {@link MinDurationPacer} so the last entry
     * lands no earlier than {@link #MIN_SOLVE_MS} (plus margin) after input
     * started.</li>
     * </ol>
     */
    public void solve(MiniSudokuRequest request) {
        if (request == null || request.request() == null) {
            throw new IllegalArgumentException("MiniSudokuRequest and its cells must not be null");
        }

        // Phase 1: fail fast, before touching the game.
        List<PendingFill> fills = validate(request.request());
        if (fills.isEmpty()) {
            return;
        }

        waitUntilReady();

        if (gameSolved())
            return;

        int totalCells = totalCellIndex();
        if (totalCells != GRID_SIZE * GRID_SIZE) {
            throw new IllegalStateException(
                    "Expected a " + GRID_SIZE + "x" + GRID_SIZE + " Sudoku grid, but found " + totalCells + " cells.");
        }

        // Phase 2: skip prefilled/already-correct cells so only real inputs are paced.
        List<PendingFill> pending = new ArrayList<>();
        for (PendingFill fill : fills) {
            if (needsInput(getCell(fill.cellIndex()), fill.value())) {
                pending.add(fill);
            }
        }
        if (pending.isEmpty()) {
            return;
        }

        // Phase 3: paced input. No DOM reads inside the loop, only click + key.
        MinDurationPacer pacer = MinDurationPacer.start(page, MIN_SOLVE_MS + SAFETY_MARGIN_MS, pending.size());

        for (int i = 0; i < pending.size(); i++) {
            PendingFill fill = pending.get(i);
            pacer.awaitStep(i);
            enterValue(getCell(fill.cellIndex()), fill.value());
        }
    }

    /**
     * Selects a cell (1-based coordinates). Does nothing for prefilled cells.
     */
    public void clickCell(int row, int column) {
        validateCoordinates(row, column);

        Locator cell = getCell(getCellIndex(row, column));
        cell.waitFor(new Locator.WaitForOptions().setTimeout(DEFAULT_TIMEOUT_MS));

        if (isPrefilled(cell)) {
            return;
        }

        cell.click();
    }

    /**
     * Enters the requested number into an editable cell (1-based coordinates).
     * Unpaced; use {@link #solve(MiniSudokuRequest)} for a full solution.
     */
    public void fillCell(int row, int column, int value) {
        validateCoordinates(row, column);
        validateValue(value);

        Locator cell = getCell(getCellIndex(row, column));
        cell.waitFor(new Locator.WaitForOptions().setTimeout(DEFAULT_TIMEOUT_MS));

        if (needsInput(cell, value)) {
            enterValue(cell, value);
        }
    }

    /**
     * Validates every answer and converts it to a {@link PendingFill}.
     */
    private List<PendingFill> validate(List<Cell> answers) {
        List<PendingFill> fills = new ArrayList<>(answers.size());
        Set<Integer> seen = new HashSet<>();

        for (Cell answer : answers) {
            if (answer == null || answer.coordinates() == null) {
                throw new IllegalArgumentException("Cell answer and coordinates must not be null");
            }

            int row = answer.coordinates().row();
            int column = answer.coordinates().column();
            int value = answer.value();

            validateCoordinates(row, column);
            validateValue(value);

            int cellIndex = getCellIndex(row, column);

            if (!seen.add(cellIndex)) {
                throw new IllegalArgumentException("Duplicate cell in request: row=" + row + ", column=" + column);
            }

            fills.add(new PendingFill(cellIndex, value));
        }
        return fills;
    }

    /**
     * True if the cell is editable and does not already hold the expected value.
     */
    private boolean needsInput(Locator cell, int value) {
        // Never overwrite a prefilled clue.
        if (isPrefilled(cell)) {
            return false;
        }

        String currentValue = cell.locator(CELL_CONTENT_SELECTOR).innerText().trim();
        return !String.valueOf(value).equals(currentValue);
    }

    /**
     * Selects the cell, then types the digit (the game takes numbers via keyboard).
     */
    private void enterValue(Locator cell, int value) {
        cell.click();
        page.keyboard().press(String.valueOf(value));
    }

    private Locator getCell(int cellIndex) {
        return boardLocator().nth(cellIndex);
    }

    private Locator boardLocator() {
        if (gameFrame == null) {
            throw new IllegalStateException("Sudoku frame has not been resolved. Call waitUntilReady() first.");
        }

        return gameFrame.locator(BOARD_SELECTOR);
    }

    private boolean isPrefilled(Locator cell) {
        String classes = cell.getAttribute("class");
        return classes != null && classes.contains(PREFILLED_CLASS);
    }

    /**
     * Maps 1-based coordinates to zero-based data-cell-idx.
     * <p>
     * (1,1) -> 0, (1,6) -> 5, (2,1) -> 6, (6,6) -> 35
     */
    private int getCellIndex(int row, int column) {
        return (row - 1) * GRID_SIZE + (column - 1);
    }

    private void validateCoordinates(int row, int column) {
        if (row < 1 || row > GRID_SIZE || column < 1 || column > GRID_SIZE) {
            throw new IllegalArgumentException("Invalid Sudoku coordinates: row=" + row + ", column=" + column
                    + ". Valid range is 1-" + GRID_SIZE + ".");
        }
    }

    private void validateValue(int value) {
        if (value < 1 || value > GRID_SIZE) {
            throw new IllegalArgumentException(
                    "Invalid Sudoku value: " + value + ". Valid values are 1-" + GRID_SIZE + ".");
        }
    }

    private boolean gameSolved() {
        return page.locator("//button[contains(normalize-space(.), 'See results')]").count() > 0;
    }
}