package com.achhecode.linkedinservice.page.games;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import com.achhecode.browserpilotdto.dto.Direction;
import com.achhecode.browserpilotdto.dto.WendResult;
import com.achhecode.linkedinservice.LinkedInGame;
import com.achhecode.linkedinservice.handler.WebsitePage;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

public class WendPage extends WebsitePage {

    private static final Logger LOGGER = Logger.getLogger(WendPage.class.getName());

    private static final String BOARD_SELECTOR = "xpath=//div[@data-game-content-root]//div[@data-testid='wend-game-board']";

    private static final String CELL_SELECTOR = BOARD_SELECTOR + "//*[@data-cell-idx]";

    private static final double DEFAULT_TIMEOUT_MS = 10_000;

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
     * Solves Wend (1-based coordinates). Everything is planned first (one DOM
     * read), then all words are traced on one timeline lasting 2.3-2.7 s in total.
     */
    public void solve(List<WendResult> words) {
        if (words == null || words.isEmpty()) {
            LOGGER.warning("Wend answer is empty; nothing to solve.");
            return;
        }

        waitUntilReady();
        int gridSize = gridSize();

        // Phase 1: validate and plan, no input yet.
        Map<Integer, double[]> centres = PacedDrags.cellCentres(page, CELL_SELECTOR);
        PacedDrags timeline = new PacedDrags();

        for (WendResult word : words) {
            timeline.drag(buildPoints(word, gridSize, centres));
        }

        // Phase 2: paced input.
        timeline.run(page);
    }

    /** Start cell centre, then the centre of each cell the directions walk onto. */
    private List<double[]> buildPoints(WendResult word, int gridSize, Map<Integer, double[]> centres) {
        if (word == null || word.startCoordinates() == null || word.directions() == null) {
            throw new IllegalArgumentException("Wend word is missing startCoordinates or directions: " + word);
        }

        String label = word.word();
        int row = word.startCoordinates().row();
        int column = word.startCoordinates().column();

        List<double[]> points = new ArrayList<>();
        points.add(centreOf(label, row, column, gridSize, centres));

        for (Direction direction : word.directions()) {
            switch (direction) {
            case UP -> row--;
            case DOWN -> row++;
            case LEFT -> column--;
            case RIGHT -> column++;
            }
            points.add(centreOf(label, row, column, gridSize, centres));
        }
        return points;
    }

    private double[] centreOf(String label, int row, int column, int gridSize, Map<Integer, double[]> centres) {
        if (!inBounds(row, column, gridSize)) {
            throw new IllegalArgumentException(label + ": " + row + "," + column + " is outside the board");
        }
        double[] centre = centres.get(toCellIndex(row, column, gridSize));
        if (centre == null) {
            throw new IllegalStateException(label + ": no cell found for row " + row + ", column " + column);
        }
        return centre;
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

    /** Row 6, column 6 on a 7x7 board is idx 40: (6-1)*7 + (6-1). */
    private int toCellIndex(int row, int column, int gridSize) {
        return (row - 1) * gridSize + (column - 1);
    }

    private boolean inBounds(int row, int column, int gridSize) {
        return row >= 1 && row <= gridSize && column >= 1 && column <= gridSize;
    }
}