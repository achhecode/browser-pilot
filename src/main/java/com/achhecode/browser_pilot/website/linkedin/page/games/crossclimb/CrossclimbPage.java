package com.achhecode.browser_pilot.website.linkedin.page.games.crossclimb;

import java.util.List;
import java.util.stream.Collectors;

import com.achhecode.browser_pilot.website.WebsitePage;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.PlaywrightException;

import lombok.extern.slf4j.Slf4j;

@Slf4j 
public class CrossclimbPage extends WebsitePage {

    private static final String GRID_SELECTOR =
            "div.crossclimb__grid-wrapper > div.crossclimb__grid";

    private static final String CLUE_SELECTOR =
            "p.crossclimb__clue";

    public CrossclimbPage(Page page) {
        super(page);
    }

    public boolean isDisplayed() {
        return page.url().contains("/games/crossclimb/");
    }

    public void waitUntilReady() {
        page.locator(GRID_SELECTOR)
                .waitFor(
                        new Locator.WaitForOptions()
                                .setTimeout(10_000)
                );
    }

    public String getCurrentClue() {
        return page.locator(CLUE_SELECTOR)
                .first()
                .textContent()
                .trim();
    }

    public boolean waitForClueToChange(String previousClue) {
        try {
            page.waitForFunction(
                    """
                    previous => {
                        const element = document.querySelector('p.crossclimb__clue');
                        return element && element.textContent.trim() !== previous;
                    }
                    """,
                    previousClue,
                    new Page.WaitForFunctionOptions()
                            .setTimeout(5_000)
            );

            return true;

        } catch (PlaywrightException e) {
            log.error(
                    "Crossclimb clue did not change within 5 seconds. Previous clue: '{}'",
                    previousClue,
                    e
            );

            return false;
        }
    }


    public List<String> getBoardWords() {
        return page.locator("div.crossclimb__guess")
                .all()
                .stream()
                .map(this::getWord)
                .toList();
    }

    private String getWord(Locator row) {
        return row.locator("input[data-crossclimb-guess-input-idx]")
                .all()
                .stream()
                .map(input -> input.inputValue())
                .collect(Collectors.joining())
                .trim();
    }

    public void arrangeWords(List<String> desiredOrder) {

        Locator rows = page.locator(
                "div.crossclimb__guess:not(.crossclimb__guess--lock)"
        );

        for (int targetIndex = 0;
            targetIndex < desiredOrder.size();
            targetIndex++) {

            String desiredWord = desiredOrder.get(targetIndex);

            int currentIndex = findRowIndex(
                    rows,
                    desiredWord
            );

            if (currentIndex == targetIndex) {
                continue;
            }

            Locator sourceRow = rows.nth(currentIndex);
            Locator targetRow = rows.nth(targetIndex);

            Locator sourceHandle = sourceRow.locator(
                    "[data-sortable-handle='true']"
            ).first();

            Locator targetHandle = targetRow.locator(
                    "[data-sortable-handle='true']"
            ).first();

            sourceHandle.dragTo(targetHandle);
        }
    }

    private int findRowIndex(
        Locator rows,
        String word
    ) {
        for (int i = 0; i < rows.count(); i++) {

            String currentWord = getWord(rows.nth(i));

            if (currentWord.equalsIgnoreCase(word)) {
                return i;
            }
        }

        throw new IllegalStateException(
                "Word not found on Crossclimb board: " + word
        );
    }
}