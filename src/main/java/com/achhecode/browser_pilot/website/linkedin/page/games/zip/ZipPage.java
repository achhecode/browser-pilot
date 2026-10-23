package com.achhecode.browser_pilot.website.linkedin.page.games.zip;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.achhecode.browser_pilot.website.WebsitePage;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class ZipPage extends WebsitePage {

    private static final String BOARD_SELECTOR = "xpath=//div[@data-testid='zip-game-container']";

    private static final double DEFAULT_TIMEOUT_MS = 10_000;

    private static final Map<String, String> DIRECTION_KEYS = Map.of("UP", "ArrowUp", "DOWN", "ArrowDown", "LEFT",
            "ArrowLeft", "RIGHT", "ArrowRight");

    public ZipPage(Page page) {
        super(page);
    }

    @Override
    public boolean isDisplayed() {
        return page.url().contains("/games/zip/");
    }

    /**
     * Wait until the Zip game board is loaded.
     */
    public void waitUntilReady() {
        page.locator(BOARD_SELECTOR).waitFor(new Locator.WaitForOptions().setTimeout(DEFAULT_TIMEOUT_MS));
    }

    /**
     * Executes the sequence of directions supplied in ZipRequest.
     */
    public void solve(ZipRequest request) {
        if (request == null || request.answer() == null) {
            throw new IllegalArgumentException("ZipRequest and its answer list must not be null");
        }

        waitUntilReady();

        List<String> directions = request.answer();

        for (int i = 0; i < directions.size(); i++) {
            String direction = directions.get(i);

            if (direction == null) {
                throw new IllegalArgumentException("Direction at index " + i + " must not be null");
            }

            String key = DIRECTION_KEYS.get(direction.trim().toUpperCase(Locale.ROOT));

            if (key == null) {
                throw new IllegalArgumentException(
                        "Invalid direction at index " + i + ": " + direction + ". Expected UP, DOWN, LEFT, or RIGHT.");
            }

            page.keyboard().press(key);
        }
    }
}
