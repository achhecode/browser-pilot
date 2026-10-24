package com.achhecode.linkedinservice.page.games;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import com.achhecode.browserpilotdto.dto.Coordinates;
import com.achhecode.browserpilotdto.dto.Direction;
import com.achhecode.browserpilotdto.dto.PatchResult;
import com.achhecode.linkedinservice.LinkedInGame;
import com.achhecode.linkedinservice.handler.WebsitePage;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

public class PatchesPage extends WebsitePage {

    private static final Logger LOGGER = Logger.getLogger(PatchesPage.class.getName());

    private static final String BOARD_SELECTOR = "xpath=//div[@data-game-content-root and @data-testid='patches-game-container']";

    private static final String CELL_SELECTOR = BOARD_SELECTOR + "//*[@data-cell-idx][@role='button']";

    private static final double DEFAULT_TIMEOUT_MS = 10_000;

    public PatchesPage(Page page) {
        super(page);
    }

    @Override
    public boolean isDisplayed() {
        return page.url().contains(LinkedInGame.PATCHES.path());
    }

    public void waitUntilReady() {
        page.locator(BOARD_SELECTOR).waitFor(new Locator.WaitForOptions().setTimeout(DEFAULT_TIMEOUT_MS));
    }

    public int totalCellIndex() {
        return page.locator(CELL_SELECTOR).count();
    }

    /**
     * Solves Patches (1-based coordinates). Everything is planned first (one DOM
     * read), then all drags run on one timeline lasting 2.3-2.7 s in total.
     */
    public void solve(List<PatchResult> patches) {
        if (patches == null || patches.isEmpty()) {
            LOGGER.warning("Patches answer is empty; nothing to solve.");
            return;
        }

        waitUntilReady();
        int gridSize = gridSize();

        // Phase 1: validate and plan, no input yet.
        validateNoOverlap(patches, gridSize);

        Map<Integer, double[]> centres = PacedDrags.cellCentres(page, CELL_SELECTOR);
        PacedDrags timeline = new PacedDrags();

        for (int i = 0; i < patches.size(); i++) {
            List<Coordinates> path = buildPath(i, patches.get(i), gridSize);
            timeline.drag(toPoints(path, gridSize, centres));
        }

        // Phase 2: paced input.
        timeline.run(page);
    }

    private List<double[]> toPoints(List<Coordinates> path, int gridSize, Map<Integer, double[]> centres) {
        List<double[]> points = new ArrayList<>(path.size());
        for (Coordinates c : path) {
            double[] centre = centres.get(toCellIndex(c.row(), c.column(), gridSize));
            if (centre == null) {
                throw new IllegalStateException("No cell found for " + c);
            }
            points.add(centre);
        }
        return points;
    }

    /** Picks the shortest route and turns it into the list of visited cells. */
    private List<Coordinates> buildPath(int index, PatchResult patch, int gridSize) {
        Coordinates start = patch.startCoordinates();
        if (start == null || !inBounds(start, gridSize)) {
            throw new IllegalArgumentException("Patch " + index + " has an invalid start: " + start);
        }
        if (patch.directions() == null || patch.directions().isEmpty()) {
            throw new IllegalArgumentException("Patch " + index + " has no directions");
        }

        List<Direction> route = patch.directions().stream().min(Comparator.comparingInt(List::size)).orElseThrow();

        List<Coordinates> path = new ArrayList<>();
        path.add(start);

        int row = start.row();
        int column = start.column();
        for (Direction direction : route) {
            switch (direction) {
            case UP -> row--;
            case DOWN -> row++;
            case LEFT -> column--;
            case RIGHT -> column++;
            }
            Coordinates next = new Coordinates(row, column);
            if (!inBounds(next, gridSize)) {
                throw new IllegalArgumentException("Patch " + index + " route leaves the board at " + next);
            }
            path.add(next);
        }

        // The drag's end cell must belong to the patch (it is the opposite corner).
        Coordinates end = path.get(path.size() - 1);
        if (patch.coordinates() != null && !patch.coordinates().contains(end)) {
            throw new IllegalArgumentException("Patch " + index + " route ends outside the patch at " + end);
        }
        return path;
    }

    private void validateNoOverlap(List<PatchResult> patches, int gridSize) {
        Set<Coordinates> seen = new HashSet<>();
        for (int i = 0; i < patches.size(); i++) {
            PatchResult patch = patches.get(i);
            if (patch == null || patch.coordinates() == null) {
                throw new IllegalArgumentException("Patch " + i + " is missing coordinates");
            }
            for (Coordinates c : patch.coordinates()) {
                if (!inBounds(c, gridSize)) {
                    throw new IllegalArgumentException(
                            "Patch " + i + " cell is outside the " + gridSize + "x" + gridSize + " board: " + c);
                }
                if (!seen.add(c)) {
                    throw new IllegalArgumentException("Cell " + c + " belongs to more than one patch");
                }
            }
        }
        if (seen.size() != gridSize * gridSize) {
            LOGGER.warning("Patches cover " + seen.size() + " cells but the board has " + gridSize * gridSize);
        }
    }

    /** Board side length; throws if the rendered cells don't form a square. */
    private int gridSize() {
        int totalCells = totalCellIndex();
        int gridSize = (int) Math.round(Math.sqrt(totalCells));

        if (totalCells == 0 || gridSize * gridSize != totalCells) {
            throw new IllegalStateException("Patches board does not appear to be square. Cell count: " + totalCells);
        }
        return gridSize;
    }

    /** Unpaced single-cell click (1-based). */
    public void clickCell(int row, int column) {
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
        cell.click();
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
            LOGGER.warning("Could not find the Patches button: " + namePattern);
        }
    }

    private Locator getCell(int cellIndex) {
        return page.locator(BOARD_SELECTOR + "//*[@data-cell-idx='" + cellIndex + "' and @role='button']").first();
    }

    private int toCellIndex(int row, int column, int gridSize) {
        return (row - 1) * gridSize + (column - 1);
    }

    private boolean inBounds(Coordinates c, int gridSize) {
        return c != null && c.row() >= 1 && c.row() <= gridSize && c.column() >= 1 && c.column() <= gridSize;
    }
}