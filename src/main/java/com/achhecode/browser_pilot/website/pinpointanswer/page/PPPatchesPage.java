
package com.achhecode.browser_pilot.website.pinpointanswer.page;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.achhecode.browser_pilot.grid.Coordinates;
import com.achhecode.browser_pilot.website.WebsitePage;
import com.achhecode.browser_pilot.website.pinpointanswer.PinpointanswerUrls;
import com.achhecode.browser_pilot.website.pinpointanswer.dto.PPPatchesResult;
import com.achhecode.browser_pilot.website.pinpointanswer.dto.PPPatchesResults;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class PPPatchesPage extends WebsitePage implements AnswerPage<PPPatchesResults> {

    private static final String REVEAL_ALL_XPATH = "//button[normalize-space(.)='Reveal all']";

    private static final String TOTAL_CELL_XPATH = "//button[contains(@class, 'patches-cell-button')]";

    private static final Pattern PATCH_LABEL_PATTERN = Pattern
            .compile("Reveal patch (\\d+) at row (\\d+), column (\\d+)");

    public PPPatchesPage(Page page) {
        super(page);
    }

    @Override
    public boolean isDisplayed() {
        return page.url().equals(PinpointanswerUrls.PATCHES);
    }

    @Override
    public PPPatchesResults getAnswer() {
        return new PPPatchesResults(getPatches());
    }

    public void clickRevealAll() {
        Locator revealButton = page.locator(REVEAL_ALL_XPATH);

        if (revealButton.count() > 0) {
            revealButton.click();
        }
    }

    public int totalCell() {
        return page.locator(TOTAL_CELL_XPATH).count();
    }

    public int calculateGridSize() {
        int total = totalCell();
        int gridSize = (int) Math.sqrt(total);

        if (total == 0 || gridSize * gridSize != total) {
            throw new IllegalStateException("Patches grid is not square. Cell count: " + total);
        }

        return gridSize;
    }

    public List<PPPatchesResult> getPatches() {
        clickRevealAll();

        // Wait for the reveal operation to finish updating the board.
        page.waitForTimeout(1000);

        Locator buttons = page.locator(TOTAL_CELL_XPATH);
        int totalCells = buttons.count();

        if (totalCells == 0) {
            throw new IllegalStateException("No Patches cells found after clicking Reveal all");
        }

        int gridSize = (int) Math.sqrt(totalCells);

        if (gridSize * gridSize != totalCells) {
            throw new IllegalStateException("Patches grid is not square. Cell count: " + totalCells);
        }

        /*
         * The aria-label contains the patch ID: "Reveal patch 1 at row 1, column 1"
         *
         * Group by patch ID instead of color or box-shadow. Cells belonging to the same
         * patch can have different shadows because the shadow indicates the patch's
         * outer boundaries.
         */
        Map<Integer, List<Cell>> patchesById = new LinkedHashMap<>();

        for (int i = 0; i < totalCells; i++) {
            Locator button = buttons.nth(i);

            String ariaLabel = button.getAttribute("aria-label");

            if (ariaLabel == null) {
                throw new IllegalStateException("Missing aria-label for cell index " + i);
            }

            Matcher matcher = PATCH_LABEL_PATTERN.matcher(ariaLabel);

            if (!matcher.matches()) {
                throw new IllegalStateException("Unexpected Patches cell aria-label: " + ariaLabel);
            }

            int patchId = Integer.parseInt(matcher.group(1));
            int row = Integer.parseInt(matcher.group(2));
            int col = Integer.parseInt(matcher.group(3));

            Integer patchSize = readPatchSize(button);

            Cell cell = new Cell(row, col, patchSize);

            patchesById.computeIfAbsent(patchId, ignored -> new ArrayList<>()).add(cell);
        }

        List<PPPatchesResult> results = new ArrayList<>();

        for (Map.Entry<Integer, List<Cell>> entry : patchesById.entrySet()) {

            results.add(buildPatch(entry.getKey(), entry.getValue()));
        }

        return results;
    }

    /**
     * Only one cell in each patch contains the clue badge. Other cells correctly
     * return null.
     */

    private Integer readPatchSize(Locator button) {
        Locator clue = button.locator(
                "xpath=.//span[contains(@class, 'patches-clue-badge')]" + "//span[contains(@class, 'text-white')]");

        if (clue.count() == 0) {
            return null;
        }

        String text = clue.first().innerText().trim();

        // Shape clues such as "wide clue, ? cells" may have no numeric text.
        if (text.isEmpty()) {
            return null;
        }

        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException exception) {
            // Non-numeric clue types are handled using the patch's cell count.
            return null;
        }
    }

    /**
     * Builds one result per patch ID. Multiple patches may have the same size, but
     * remain separate results.
     */

    @SuppressWarnings("null")
    private PPPatchesResult buildPatch(int patchId, List<Cell> cells) {
        Integer clueSize = null;
        List<Coordinates> coordinates = new ArrayList<>();

        for (Cell cell : cells) {
            if (cell.patchSize() != null) {
                if (clueSize != null && !clueSize.equals(cell.patchSize())) {
                    throw new IllegalStateException("Conflicting clues for patch " + patchId);
                }
                clueSize = cell.patchSize();
            }

            coordinates.add(new Coordinates(cell.row(), cell.col()));
        }

        int actualSize = coordinates.size();

        if (clueSize != null && clueSize != actualSize) {
            throw new IllegalStateException(
                    "Patch " + patchId + " has clue size " + clueSize + " but contains " + actualSize + " cells.");
        }

        // Use row-major order for a deterministic starting cell.
        coordinates.sort(java.util.Comparator.comparingInt(Coordinates::row).thenComparingInt(Coordinates::column));

        Coordinates start = coordinates.get(0);
        Coordinates end = coordinates.get(coordinates.size() - 1);

        List<List<PPPatchesResult.Direction>> paths = findAllPaths(coordinates, start, end);

        return new PPPatchesResult(actualSize, coordinates, start, paths);
    }

    private List<List<PPPatchesResult.Direction>> findAllPaths(List<Coordinates> patchCoordinates, Coordinates start,
            Coordinates end) {
        Set<Coordinates> patchCells = new HashSet<>(patchCoordinates);

        List<List<PPPatchesResult.Direction>> paths = new ArrayList<>();
        List<PPPatchesResult.Direction> currentPath = new ArrayList<>();
        Set<Coordinates> visited = new HashSet<>();

        findPathsDfs(start, end, patchCells, visited, currentPath, paths);

        return paths;
    }

    private void findPathsDfs(Coordinates current, Coordinates end, Set<Coordinates> patchCells,
            Set<Coordinates> visited, List<PPPatchesResult.Direction> currentPath,
            List<List<PPPatchesResult.Direction>> paths) {
        if (current.equals(end)) {
            paths.add(new ArrayList<>(currentPath));
            return;
        }

        visited.add(current);

        // The order determines which valid path is listed first.
        tryDirection(current, end, 0, 1, PPPatchesResult.Direction.RIGHT, patchCells, visited, currentPath, paths);

        tryDirection(current, end, 1, 0, PPPatchesResult.Direction.DOWN, patchCells, visited, currentPath, paths);

        tryDirection(current, end, 0, -1, PPPatchesResult.Direction.LEFT, patchCells, visited, currentPath, paths);

        tryDirection(current, end, -1, 0, PPPatchesResult.Direction.UP, patchCells, visited, currentPath, paths);

        visited.remove(current);
    }

    private void tryDirection(Coordinates current, Coordinates end, int rowDelta, int colDelta,
            PPPatchesResult.Direction direction, Set<Coordinates> patchCells, Set<Coordinates> visited,
            List<PPPatchesResult.Direction> currentPath, List<List<PPPatchesResult.Direction>> paths) {
        Coordinates next = new Coordinates(current.row() + rowDelta, current.column() + colDelta);

        if (!patchCells.contains(next) || visited.contains(next)) {
            return;
        }

        currentPath.add(direction);

        findPathsDfs(next, end, patchCells, visited, currentPath, paths);

        currentPath.remove(currentPath.size() - 1);
    }

    private record Cell(int row, int col, Integer patchSize) {
    }
}