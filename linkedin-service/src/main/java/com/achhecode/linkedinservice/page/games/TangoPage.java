package com.achhecode.linkedinservice.page.games;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import com.achhecode.browserpilotdto.dto.Coordinates;
import com.achhecode.browserpilotdto.dto.TangoRequest;
import com.achhecode.browserpilotdto.dto.TangoResult;
import com.achhecode.playwrightservice.handler.WebsitePage;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class TangoPage extends WebsitePage {

    private static final String BOARD_SELECTOR = "xpath=//div[@data-testid='tango-game-container']";

    private static final String CELL_SELECTOR = "[data-cell-idx][role='button']";

    private static final int GRID_SIZE = 6;

    private static final double DEFAULT_TIMEOUT_MS = 10_000;

    /** Minimum solve time (2 s) plus a margin for network/render latency. */
    private static final long MIN_SOLVE_MS = 2_000;
    private static final long SAFETY_MARGIN_MS = 150;

    /** Cell symbols. The click count is how many clicks place the symbol. */
    private enum Symbol {
        SUN(1), MOON(2);

        final int clicks;

        Symbol(int clicks) {
            this.clicks = clicks;
        }

        static Symbol parse(String value) {
            if (value == null) {
                throw new IllegalArgumentException("Cell value must not be null");
            }
            try {
                return valueOf(value.trim().toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Unsupported Tango value: " + value + ". Expected SUN or MOON.");
            }
        }
    }

    /** A validated, ready-to-run placement. */
    private record Step(int cellIndex, Symbol symbol) {
    }

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
     * Applies all answers from the supplied TangoRequest (1-based coordinates).
     * <p>
     * SUN -> click once, MOON -> click twice. Phases:
     * <ol>
     * <li>validate the whole request (no page interaction),</li>
     * <li>drop cells that are already disabled/locked,</li>
     * <li>place the rest, paced by {@link MinDurationPacer} so the last placement
     * lands no earlier than {@link #MIN_SOLVE_MS} (plus margin) after input
     * started.</li>
     * </ol>
     */
    public void solve(TangoRequest request) {
        if (request == null || request.request() == null) {
            throw new IllegalArgumentException("TangoRequest and its answer list must not be null");
        }

        // Phase 1: fail fast, before touching the game.
        List<Step> steps = validate(request.request());
        if (steps.isEmpty()) {
            return;
        }

        waitUntilReady();

        // Phase 2: skip locked cells. DOM reads happen here, not in the timed loop.
        List<Step> pending = new ArrayList<>(steps.size());
        for (Step step : steps) {
            if (!isDisabled(getCell(step.cellIndex()))) {
                pending.add(step);
            }
        }
        if (pending.isEmpty()) {
            return;
        }

        // Phase 3: paced input, clicks only.
        MinDurationPacer pacer = MinDurationPacer.start(page, MIN_SOLVE_MS + SAFETY_MARGIN_MS, pending.size());

        for (int i = 0; i < pending.size(); i++) {
            Step step = pending.get(i);
            pacer.awaitStep(i);
            place(getCell(step.cellIndex()), step.symbol());
        }
    }

    /**
     * Clicks the cell once to place a Sun (unpaced, 1-based coordinates).
     */
    public void putSun(int row, int column) {
        putSingle(row, column, Symbol.SUN);
    }

    /**
     * Clicks the cell twice to place a Moon (unpaced, 1-based coordinates).
     */
    public void putMoon(int row, int column) {
        putSingle(row, column, Symbol.MOON);
    }

    private void putSingle(int row, int column, Symbol symbol) {
        validateCoordinates(row, column);

        Locator cell = getCell(getCellIndex(row, column));
        cell.waitFor(new Locator.WaitForOptions().setTimeout(DEFAULT_TIMEOUT_MS));

        if (!isDisabled(cell)) {
            place(cell, symbol);
        }
    }

    /**
     * Clicks the cell as many times as the symbol needs, stopping early if the cell
     * becomes disabled. Locators are lazy, so the same instance always reflects the
     * current DOM and does not need re-querying.
     */
    private void place(Locator cell, Symbol symbol) {
        for (int click = 0; click < symbol.clicks; click++) {
            if (isDisabled(cell)) {
                return;
            }
            cell.click();
        }
    }

    /**
     * Validates every answer (including duplicates) and converts it to a Step.
     */
    private List<Step> validate(List<TangoResult> answers) {
        List<Step> steps = new ArrayList<>(answers.size());
        Set<Integer> seen = new HashSet<>();

        for (TangoResult answer : answers) {
            if (answer == null || answer.coordinates() == null) {
                throw new IllegalArgumentException("Answer and coordinates must not be null");
            }

            Coordinates coordinates = answer.coordinates();
            int row = coordinates.row();
            int column = coordinates.column();

            validateCoordinates(row, column);

            int cellIndex = getCellIndex(row, column);

            if (!seen.add(cellIndex)) {
                throw new IllegalArgumentException("Duplicate cell in request: row=" + row + ", column=" + column);
            }

            steps.add(new Step(cellIndex, Symbol.parse(answer.value())));
        }
        return steps;
    }

    private Locator getCell(int cellIndex) {
        return page.locator(BOARD_SELECTOR).locator("[data-cell-idx='" + cellIndex + "'][role='button']");
    }

    private boolean isDisabled(Locator cell) {
        return "true".equalsIgnoreCase(cell.getAttribute("aria-disabled"));
    }

    private void validateCoordinates(int row, int column) {
        if (row < 1 || row > GRID_SIZE || column < 1 || column > GRID_SIZE) {
            throw new IllegalArgumentException("Invalid Tango coordinates: row=" + row + ", column=" + column
                    + ". Valid range is 1-" + GRID_SIZE + ".");
        }
    }

    /**
     * Maps 1-based grid coordinates to the DOM data-cell-idx (row-major order).
     * <p>
     * IMPORTANT: verify this against the live board. If LinkedIn numbers cells
     * differently, this is the only method to change.
     */
    private int getCellIndex(int row, int column) {
        return (row - 1) * GRID_SIZE + (column - 1);
    }
}