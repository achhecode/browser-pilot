package com.achhecode.browser_pilot.website.pinpointanswer.page;

import com.achhecode.browser_pilot.website.WebsitePage;
import com.achhecode.browser_pilot.website.pinpointanswer.PinpointanswerUrls;
import com.achhecode.browser_pilot.website.pinpointanswer.dto.PPZipResult;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class PPZipPage extends WebsitePage implements AnswerPage<PPZipResult> {

    private static final String NEXT_STEP_XPATH = "//button[normalize-space(.)='Next step']";

    private static final String REVEAL_ALL_XPATH = "//button[normalize-space(.)='Reveal all']";

    private static final String CLEAR_ALL_XPATH = "//button[normalize-space(.)='Clear all']";

    private static final String TOTAL_CELL_XPATH = "//button[@data-zip-answer-cell]";

    private static final String PATH_STEP_XPATH = "//button[contains(concat(' ', normalize-space(@aria-label), ' '), ' path step %d ')]";

    public PPZipPage(Page page) {
        super(page);
    }

    public boolean isDisplayed() {
        return page.url().equals(PinpointanswerUrls.ZIP);
    }

    @Override 
    public PPZipResult getAnswer() {
        return new PPZipResult(getPathToCompleteZip());
    }

    public void clickNextStep() {
        page.locator(NEXT_STEP_XPATH).click();
    }

    public void clickRevealAll() {
        page.locator(REVEAL_ALL_XPATH).click();
    }

    public void clickClearAll() {
        page.locator(CLEAR_ALL_XPATH).click();
    }

    public int totalCell() {
        return page.locator(TOTAL_CELL_XPATH).count();
    }

    public int calculateGridSize() {
        int total = totalCell();
        int gridSize = (int) Math.sqrt(total);

        if (gridSize * gridSize != total) {
            throw new IllegalStateException("Grid cells do not form a square. Total cells: " + total);
        }

        return gridSize;
    }

    public String getPathToCompleteZip() {
        clickRevealAll();
        int gridSize = calculateGridSize();
        int total = totalCell();

        StringBuilder path = new StringBuilder();

        int previousRow = -1;
        int previousCol = -1;

        for (int step = 1; step <= total; step++) {

            String xpath = String.format(PATH_STEP_XPATH, step);
            Locator stepLocator = page.locator(xpath);

            if (stepLocator.count() == 0) {
                break;
            }

            String cellValue = stepLocator.first().getAttribute("data-zip-answer-cell");

            if (cellValue == null || cellValue.isBlank()) {
                continue;
            }

            String[] coordinates = cellValue.split("-");

            if (coordinates.length != 2) {
                throw new IllegalStateException("Invalid cell coordinates: " + cellValue);
            }

            int row = Integer.parseInt(coordinates[0]);
            int col = Integer.parseInt(coordinates[1]);

            if (row < 1 || row > gridSize || col < 1 || col > gridSize) {
                throw new IllegalStateException("Cell coordinates outside grid: " + cellValue);
            }

            if (previousRow != -1) {
                String direction = getDirection(previousRow, previousCol, row, col);

                if (path.length() > 0) {
                    path.append(",");
                }

                path.append(direction);
            }

            previousRow = row;
            previousCol = col;
        }

        return path.toString();
    }

    // Determine movement between two consecutive cells
    private String getDirection(int previousRow, int previousCol, int currentRow, int currentCol) {

        if (currentRow == previousRow && currentCol == previousCol + 1) {
            return "RIGHT";
        }

        if (currentRow == previousRow && currentCol == previousCol - 1) {
            return "LEFT";
        }

        if (currentCol == previousCol && currentRow == previousRow + 1) {
            return "DOWN";
        }

        if (currentCol == previousCol && currentRow == previousRow - 1) {
            return "UP";
        }

        throw new IllegalStateException(
                "Non-adjacent path cells: " + previousRow + "-" + previousCol + " -> " + currentRow + "-" + currentCol);
    }

}
