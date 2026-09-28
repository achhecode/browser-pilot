package com.achhecode.browser_pilot.website.linkedin.page;

import com.achhecode.browser_pilot.website.WebsitePage;
import com.achhecode.browser_pilot.website.linkedin.LinkedInUrls;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class MyNetworkPage extends WebsitePage {

    public MyNetworkPage(Page page) {
        super(page);
    }

    public void open() {
        if (!page.url().contains("/mynetwork")) {
            page.navigate(LinkedInUrls.MY_NETWORK);
        }
    }

    public boolean isDisplayed() {
        return page.url().contains("/mynetwork");
    }

    public void clickCrossclimb() {
        clickGame("/games/crossclimb/");
    }

    public void clickPatches() {
        clickGame("/games/patches/");
    }

    public void clickZip() {
        clickGame("/games/zip/");
    }

    public void clickMiniSudoku() {
        clickGame("/games/mini-sudoku/");
    }

    public void clickWend() {
        clickGame("/games/wend/");
    }

    public void clickTango() {
        clickGame("/games/tango/");
    }

    public void clickQueens() {
        clickGame("/games/queens/");
    }

    private void clickGame(String path) {

        System.out.println("Current URL: " + page.url());
        System.out.println("Current title: " + page.title());

        Locator game =
                page.locator("a[href='" + path + "']").first();

        System.out.println(
                "Matching game links: " + game.count()
        );

        // page.locator("a[href='" + path + "']")
        //         .first()
        //         .click(new Locator.ClickOptions()
        //             .setTimeout(10_000));

        // Matches <a href="/games/zip/"> but doesn't <a href="https://www.linkedin.com/games/zip/">

        page.locator("a[href$='" + path + "']")
            .first()
            .click(new Locator.ClickOptions()
                    .setTimeout(10_000));

        // Matches both <a href="/games/zip/"> and <a href="https://www.linkedin.com/games/zip/">
        
    }
}