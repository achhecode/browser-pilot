package com.achhecode.linkedinservice.page.games;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.achhecode.browserpilotdto.dto.ZipRequest;
import com.achhecode.playwrightservice.handler.WebsitePage;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class ZipPage extends WebsitePage {

    private static final String BOARD_SELECTOR = "xpath=//div[@data-testid='zip-game-container']";

    private static final double DEFAULT_TIMEOUT_MS = 10_000;

    /** Minimum solve time (2 s) plus a margin for network/render latency. */
    private static final long MIN_SOLVE_MS = 2_000;
    private static final long SAFETY_MARGIN_MS = 150;

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
     * <p>
     * Keys are paced evenly by {@link MinDurationPacer} so the final key is pressed
     * no earlier than {@link #MIN_SOLVE_MS} (plus a small safety margin) after
     * solving started. All input is validated before any key is pressed.
     */
    public void solve(ZipRequest request) {
        if (request == null || request.request() == null) {
            throw new IllegalArgumentException("ZipRequest and its answer list must not be null");
        }

        // Fail fast: validate and convert everything before touching the game.
        List<String> keys = toKeys(request.request());
        if (keys.isEmpty()) {
            return;
        }

        waitUntilReady();

        MinDurationPacer pacer = MinDurationPacer.start(page, MIN_SOLVE_MS + SAFETY_MARGIN_MS, keys.size());

        for (int i = 0; i < keys.size(); i++) {
            pacer.awaitStep(i);
            page.keyboard().press(keys.get(i));
        }
    }

    /**
     * Validates directions and converts them to Playwright key names.
     */
    private static List<String> toKeys(List<String> directions) {
        List<String> keys = new ArrayList<>(directions.size());

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

            keys.add(key);
        }
        return keys;
    }
}