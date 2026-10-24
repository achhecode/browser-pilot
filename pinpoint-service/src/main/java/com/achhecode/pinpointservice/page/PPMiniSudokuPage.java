
package com.achhecode.pinpointservice.page;

import java.util.ArrayList;
import java.util.List;

import com.achhecode.browserpilotdto.dto.Cell;
import com.achhecode.browserpilotdto.dto.Coordinates;
import com.achhecode.browserpilotdto.dto.PPMiniSudokuResult;
import com.achhecode.pinpointservice.PinpointanswerUrls;
import com.achhecode.playwrightservice.handler.WebsitePage;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class PPMiniSudokuPage extends WebsitePage implements AnswerPage<PPMiniSudokuResult> {

    private static final String TOTAL_CELL_XPATH = "//section//div[contains(@class, 'grid')]"
            + "//div[contains(@class, 'aspect-square')]";

    public PPMiniSudokuPage(Page page) {
        super(page);
    }

    @Override
    public boolean isDisplayed() {
        return page.url().equals(PinpointanswerUrls.MINI_SUDOKU);
    }

    @Override
    public PPMiniSudokuResult getAnswer() {
        return new PPMiniSudokuResult(getSudokuGrid());
    }

    /**
     * Returns the total number of cells in the Sudoku grid.
     */
    public int totalCell() {
        return page.locator(TOTAL_CELL_XPATH).count();
    }

    /**
     * Calculates the grid size.
     *
     * For example: 9 cells -> 3x3 36 cells -> 6x6 81 cells -> 9x9
     */
    public int calculateGridSize() {
        int total = totalCell();
        int gridSize = (int) Math.sqrt(total);

        if (total == 0 || gridSize * gridSize != total) {
            throw new IllegalStateException("Sudoku cells do not form a square. Total cells: " + total);
        }

        return gridSize;
    }

    /**
     * Extracts Sudoku values in row-major order.
     *
     * Coordinates are 1-based.
     */
    public List<Cell> getSudokuGrid() {
        Locator cells = page.locator(TOTAL_CELL_XPATH);

        int total = cells.count();
        int gridSize = calculateGridSize();

        List<Cell> result = new ArrayList<>(total);

        for (int index = 0; index < total; index++) {
            Locator cell = cells.nth(index);

            String text = cell.innerText().trim();

            if (text.isEmpty()) {
                throw new IllegalStateException(
                        "Empty Sudoku cell at row " + (index / gridSize + 1) + ", column " + (index % gridSize + 1));
            }

            int value;

            try {
                value = Integer.parseInt(text);
            } catch (NumberFormatException exception) {
                throw new IllegalStateException("Invalid Sudoku value '" + text + "' at row " + (index / gridSize + 1)
                        + ", column " + (index % gridSize + 1), exception);
            }

            int row = index / gridSize + 1;
            int column = index % gridSize + 1;

            result.add(new Cell(value, new Coordinates(row, column)));
        }

        return result;
    }
}
