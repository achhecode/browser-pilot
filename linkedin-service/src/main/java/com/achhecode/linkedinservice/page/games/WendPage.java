package com.achhecode.linkedinservice.page.games;

import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import com.achhecode.browserpilotdto.dto.Coordinates;
import com.achhecode.browserpilotdto.dto.Direction;
import com.achhecode.browserpilotdto.dto.WendResult;
import com.achhecode.linkedinservice.LinkedInGame;
import com.achhecode.linkedinservice.handler.WebsitePage;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Mouse;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.BoundingBox;

public class WendPage extends WebsitePage {

    private static final Logger LOGGER = Logger.getLogger(WendPage.class.getName());

    private static final String BOARD_SELECTOR = "xpath=//div[@data-game-content-root]//div[@data-testid='wend-game-board']";

    private static final String CELL_SELECTOR = BOARD_SELECTOR + "//*[@data-cell-idx]";

    private static final double DEFAULT_TIMEOUT_MS = 10_000;

    /** Mouse interpolation steps between two adjacent cell centres. */
    private static final int MOVE_STEPS = 8;

    /** Pause after press, after each move, and after release (ms). */
    private static final double PRESS_PAUSE_MS = 80;
    private static final double MOVE_PAUSE_MS = 50;
    private static final double RELEASE_PAUSE_MS = 120;

    public WendPage(Page page) {
        super(page);
    }

    @Override
    public boolean isDisplayed() {
        return page.url().contains(LinkedInGame.WEND.path());
    }

    public void waitUntilReady() {
        page.locator(BOARD_SELECTOR).waitFor(new Locator.WaitForOptions().setTimeout(DEFAULT_TIMEOUT_MS));
        page.locator(CELL_SELECTOR).first().waitFor(new Locator.WaitForOptions().setTimeout(DEFAULT_TIMEOUT_MS));
    }

    public int totalCellIndex() {
        return page.locator(CELL_SELECTOR).count();
    }

    /**
     * Solves Wend (1-based coordinates). For every word: press and hold on the
     * start cell, move onto the next cell for each direction, then release.
     */
    public void solve(List<WendResult> words) {
        if (words == null || words.isEmpty()) {
            LOGGER.warning("Wend answer is empty; nothing to solve.");
            return;
        }

        waitUntilReady();
        int gridSize = gridSize();
        LOGGER.info("Wend board " + gridSize + "x" + gridSize);

        // Fail fast, before touching the game.
        for (WendResult word : words) {
            validate(word, gridSize);
        }

        for (WendResult word : words) {
            traceWord(word, gridSize);
        }
    }

    /** Hold the mouse on the start cell, glide through each direction, release. */
    private void traceWord(WendResult word, int gridSize) {
        Coordinates start = word.startCoordinates();
        int row = start.row();
        int column = start.column();

        LOGGER.info("Tracing " + word.word() + " from " + start);

        double[] first = centre(row, column, gridSize);
        page.mouse().move(first[0], first[1]);
        page.mouse().down();
        page.waitForTimeout(PRESS_PAUSE_MS);

        try {
            for (Direction direction : word.directions()) {
                switch (direction) {
                case UP -> row--;
                case DOWN -> row++;
                case LEFT -> column--;
                case RIGHT -> column++;
                }

                double[] next = centre(row, column, gridSize);
                page.mouse().move(next[0], next[1], new Mouse.MoveOptions().setSteps(MOVE_STEPS));
                page.waitForTimeout(MOVE_PAUSE_MS);
            }
        } finally {
            page.mouse().up(); // release, even if a move failed
            page.waitForTimeout(RELEASE_PAUSE_MS);
        }
    }

    /** Viewport centre of a cell (1-based row/column). */
    private double[] centre(int row, int column, int gridSize) {
        Locator cell = getCell(toCellIndex(row, column, gridSize));
        cell.scrollIntoViewIfNeeded();
        BoundingBox box = cell.boundingBox();

        if (box == null) {
            throw new IllegalStateException("Cell not visible: row " + row + ", column " + column);
        }
        return new double[] { box.x + box.width / 2, box.y + box.height / 2 };
    }

    /**
     * Only checks the shape of the request and that the walk stays on the board.
     */
    private void validate(WendResult word, int gridSize) {
        if (word == null || word.startCoordinates() == null || word.directions() == null) {
            throw new IllegalArgumentException("Wend word is missing startCoordinates or directions: " + word);
        }

        String label = word.word();
        int row = word.startCoordinates().row();
        int column = word.startCoordinates().column();

        if (!inBounds(row, column, gridSize)) {
            throw new IllegalArgumentException(label + ": start " + row + "," + column + " is outside the board");
        }

        for (int i = 0; i < word.directions().size(); i++) {
            switch (word.directions().get(i)) {
            case UP -> row--;
            case DOWN -> row++;
            case LEFT -> column--;
            case RIGHT -> column++;
            }
            if (!inBounds(row, column, gridSize)) {
                throw new IllegalArgumentException(
                        label + ": step " + (i + 1) + " leaves the board at " + row + "," + column);
            }
        }
    }

    /**
     * Board side length. Hole cells are still in the DOM with a data-cell-idx, so
     * the highest index gives the size (idx 48 -> 49 cells -> 7x7).
     */
    private int gridSize() {
        Object raw = page.locator(CELL_SELECTOR)
                .evaluateAll("els => Math.max(...els.map(e => Number(e.getAttribute('data-cell-idx'))))");

        int maxIdx = raw instanceof Number n ? n.intValue() : -1;
        if (maxIdx < 0) {
            throw new IllegalStateException("Wend board has no cells with data-cell-idx");
        }

        int size = (int) Math.ceil(Math.sqrt(maxIdx + 1));
        if (size * size != maxIdx + 1) {
            LOGGER.warning("Highest cell idx " + maxIdx + " does not form a square board; using size " + size);
        }
        return size;
    }

    public void reset() {
        clickButton("reset");
    }

    public void submit() {
        clickButton("check|submit|submit answer");
    }

    private void clickButton(String namePattern) {
        Locator button = page.getByRole(AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName(Pattern.compile(namePattern, Pattern.CASE_INSENSITIVE)));

        if (button.count() > 0) {
            button.first().click();
        } else {
            LOGGER.warning("Could not find the Wend button: " + namePattern);
        }
    }

    private Locator getCell(int cellIndex) {
        return page.locator(BOARD_SELECTOR + "//*[@data-cell-idx='" + cellIndex + "']").first();
    }

    /** Row 6, column 6 on a 7x7 board is idx 40: (6-1)*7 + (6-1). */
    private int toCellIndex(int row, int column, int gridSize) {
        return (row - 1) * gridSize + (column - 1);
    }

    private boolean inBounds(int row, int column, int gridSize) {
        return row >= 1 && row <= gridSize && column >= 1 && column <= gridSize;
    }
}