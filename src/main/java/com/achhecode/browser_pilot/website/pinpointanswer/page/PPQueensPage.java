
package com.achhecode.browser_pilot.website.pinpointanswer.page;

import java.util.ArrayList;
import java.util.List;

import com.achhecode.browser_pilot.grid.Coordinates;
import com.achhecode.browser_pilot.website.WebsitePage;
import com.achhecode.browser_pilot.website.pinpointanswer.PinpointanswerUrls;
import com.achhecode.browser_pilot.website.pinpointanswer.dto.PPQueensResult;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class PPQueensPage extends WebsitePage implements AnswerPage<PPQueensResult> {

    private static final String REVEAL_ALL_XPATH =
            "//button[normalize-space(.)='Reveal all']";

    private static final String CLEAR_ALL_XPATH =
            "//button[normalize-space(.)='Clear all']";

    private static final String TOTAL_CELL_XPATH =
            "//button[@data-queens-answer-cell]";

    private static final String CELL_XPATH =
            "//button[@data-queens-answer-cell='%d-%d']";

    public PPQueensPage(Page page) {
        super(page);
    }

    public boolean isDisplayed() {
        return page.url().equals(PinpointanswerUrls.QUEENS);
    }

    @Override
    public PPQueensResult getAnswer() {
        return new PPQueensResult(getCoordinatesOfPlacedQueen());
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
            throw new IllegalStateException(
                    "Grid cells do not form a square. Total cells: " + total
            );
        }

        return gridSize;
    }

    public List<Coordinates> getCoordinatesOfPlacedQueen() {
        clickRevealAll();

        int gridSize = calculateGridSize();
        List<Coordinates> queens = new ArrayList<>();

        for (int row = 1; row <= gridSize; row++) {
            for (int col = 1; col <= gridSize; col++) {

                String xpath = String.format(CELL_XPATH, row, col);
                Locator cell = page.locator(xpath);

                String ariaLabel = cell.getAttribute("aria-label");

                if (ariaLabel != null
                        && ariaLabel.trim().toLowerCase().endsWith("queen")) {
                    queens.add(new Coordinates(row, col));
                }
            }
        }

        return queens;
    }
}
