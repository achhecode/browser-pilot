package com.achhecode.linkedinservice.page.games;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.achhecode.browserpilotdto.dto.Cell;
import com.achhecode.browserpilotdto.dto.MiniSudokuRequest;
import com.achhecode.linkedinservice.handler.WebsitePage;
import com.microsoft.playwright.Frame;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.PlaywrightException;
import com.microsoft.playwright.options.WaitForSelectorState;

public class MiniSudokuPage extends WebsitePage {

    private static final String BOARD_SELECTOR = "xpath=//section//div[contains(@class, 'sudoku-cell') "
            + "and @data-cell-idx]";

    private static final String PREFILLED_CLASS = "sudoku-cell-prefilled";
    private static final String CELL_CONTENT_SELECTOR = ".sudoku-cell-content";
    private static final String RESULTS_BUTTON_SELECTOR = "//button[contains(normalize-space(.), 'See results')]";

    private static final int GRID_SIZE = 6;
    private static final int EXPECTED_CELL_COUNT = GRID_SIZE * GRID_SIZE;

    private static final long DEFAULT_TIMEOUT_MS = 10_000L;
    private static final long POLL_INTERVAL_MS = 200L;

    private static final long MIN_SOLVE_MS = 2_000L;
    private static final long SAFETY_MARGIN_MS = 150L;

    private Locator boardCells;
    private Frame boardFrame;

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
     * Resolves the board in the main page first, then child frames.
     */
    public void waitUntilReady() {
        long deadline = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(DEFAULT_TIMEOUT_MS);

        while (System.nanoTime() < deadline) {

            Locator mainBoard = page.locator(BOARD_SELECTOR);

            if (isBoardReady(mainBoard)) {
                boardCells = mainBoard;
                boardFrame = page.mainFrame();
                return;
            }

            for (Frame frame : page.frames()) {
                if (frame == page.mainFrame()) {
                    continue;
                }

                Locator frameBoard = frame.locator(BOARD_SELECTOR);

                if (isBoardReady(frameBoard)) {
                    boardCells = frameBoard;
                    boardFrame = frame;
                    return;
                }
            }

            page.waitForTimeout(POLL_INTERVAL_MS);
        }

        throw new IllegalStateException(
                "Sudoku board not ready in the main page or any child frame. URL: " + page.url());
    }

    private boolean isBoardReady(Locator cells) {
        try {
            return cells.count() == EXPECTED_CELL_COUNT && cells.first().isVisible();
        } catch (PlaywrightException e) {
            return false;
        }
    }

    public int totalCellIndex() {
        return boardLocator().count();
    }

    /**
     * Validates all answers, skips prefilled/correct cells, then enters remaining
     * values at a controlled pace.
     */
    public void solve(MiniSudokuRequest request) {
        if (request == null || request.request() == null) {
            throw new IllegalArgumentException("MiniSudokuRequest and its cells must not be null");
        }

        List<PendingFill> fills = validate(request.request());

        if (fills.isEmpty()) {
            return;
        }

        waitUntilReady();

        if (gameSolved()) {
            return;
        }

        int totalCells = totalCellIndex();

        if (totalCells != EXPECTED_CELL_COUNT) {
            throw new IllegalStateException(
                    "Expected a " + GRID_SIZE + "x" + GRID_SIZE + " Sudoku grid, but found " + totalCells + " cells.");
        }

        List<PendingFill> pending = new ArrayList<>();

        for (PendingFill fill : fills) {
            if (needsInput(getCell(fill.cellIndex()), fill.value())) {
                pending.add(fill);
            }
        }

        if (pending.isEmpty()) {
            return;
        }

        MinDurationPacer pacer = MinDurationPacer.start(page, MIN_SOLVE_MS + SAFETY_MARGIN_MS, pending.size());

        for (int i = 0; i < pending.size(); i++) {
            PendingFill fill = pending.get(i);

            pacer.awaitStep(i);

            enterValue(getCell(fill.cellIndex()), fill.value());
        }
    }

    public void clickCell(int row, int column) {
        validateCoordinates(row, column);
        waitUntilReady();

        Locator cell = getCell(getCellIndex(row, column));

        cell.waitFor(
                new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(DEFAULT_TIMEOUT_MS));

        if (isPrefilled(cell)) {
            return;
        }

        cell.click();
    }

    public void fillCell(int row, int column, int value) {
        validateCoordinates(row, column);
        validateValue(value);
        waitUntilReady();

        Locator cell = getCell(getCellIndex(row, column));

        cell.waitFor(
                new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(DEFAULT_TIMEOUT_MS));

        if (needsInput(cell, value)) {
            enterValue(cell, value);
        }
    }

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

    private boolean needsInput(Locator cell, int value) {
        if (isPrefilled(cell)) {
            return false;
        }

        String currentValue = cell.locator(CELL_CONTENT_SELECTOR).innerText().trim();

        return !String.valueOf(value).equals(currentValue);
    }

    /**
     * Clicks the cell and sends the digit through the page keyboard.
     * Page.keyboard() is used for both main-page and iframe boards.
     */
    private void enterValue(Locator cell, int value) {
        cell.click();

        if (boardFrame == null) {
            throw new IllegalStateException("Board frame has not been resolved.");
        }

        // Keyboard input is page-scoped. Clicking the frame's cell first
        // should give the game the appropriate focus.
        page.keyboard().press(String.valueOf(value));
    }

    private Locator getCell(int cellIndex) {
        return boardLocator().nth(cellIndex);
    }

    private Locator boardLocator() {
        if (boardCells == null) {
            throw new IllegalStateException("Call waitUntilReady() before accessing the Sudoku board.");
        }

        return boardCells;
    }

    private boolean isPrefilled(Locator cell) {
        String classes = cell.getAttribute("class");

        return classes != null && classes.contains(PREFILLED_CLASS);
    }

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
                    "Invalid Sudoku value: " + value + ". Valid range is 1-" + GRID_SIZE + ".");
        }
    }

    private boolean gameSolved() {
        if (page.locator(RESULTS_BUTTON_SELECTOR).count() > 0) {
            return true;
        }

        if (boardFrame != null && boardFrame != page.mainFrame()) {
            return boardFrame.locator(RESULTS_BUTTON_SELECTOR).count() > 0;
        }

        return false;
    }
}
