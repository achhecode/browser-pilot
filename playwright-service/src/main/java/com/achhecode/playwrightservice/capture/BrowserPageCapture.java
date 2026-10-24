package com.achhecode.playwrightservice.capture;

import com.achhecode.playwrightservice.browser.TabManager;
import com.microsoft.playwright.Page;
import org.springframework.stereotype.Component;

@Component
public class BrowserPageCapture {

        private final TabManager tabManager;

        public BrowserPageCapture(TabManager tabManager) {
                this.tabManager = tabManager;
        }

        public CapturedPage capture() {
                return tabManager.withCurrentOpenTab(page -> {
                        if (page.isClosed()) {
                                throw new IllegalStateException("The browser page is already closed.");
                        }

                        String url = page.url();
                        String title = page.title();
                        String html = page.content();

                        byte[] screenshot = page.screenshot(new Page.ScreenshotOptions().setFullPage(true));

                        return new CapturedPage(url, title, html, screenshot);
                });
        }

        public record CapturedPage(String url, String title, String html, byte[] screenshot) {
        }
}