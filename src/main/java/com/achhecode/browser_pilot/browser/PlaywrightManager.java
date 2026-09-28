package com.achhecode.browser_pilot.browser;

import com.achhecode.browser_pilot.config.BrowserPilotProperties;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

@Component
public class PlaywrightManager {

    private final BrowserPilotProperties properties;

    private Playwright playwright;

    private Browser browser;

    public PlaywrightManager(
            BrowserPilotProperties properties
    ) {
        this.properties = properties;
    }

    @PostConstruct
    public void start() {

        playwright = Playwright.create();

        if (properties.getDebug().isPersistent()) {

            connectToPersistentBrowser();

        } else {

            launchManagedBrowser();
        }
    }

    private void connectToPersistentBrowser() {

        String endpoint =
                "http://127.0.0.1:"
                        + properties.getDebug().getPort();

        System.out.println(
                "Connecting to Chromium at " + endpoint
        );

        browser = playwright.chromium()
                .connectOverCDP(endpoint);

        System.out.println(
                "Connected to existing Chromium."
        );
    }

    private void launchManagedBrowser() {

        browser = playwright.chromium().launch(
                new com.microsoft.playwright.BrowserType.LaunchOptions()
                        .setHeadless(properties.isHeadless())
        );

        System.out.println(
                "Started managed Chromium."
        );
    }

    public Browser getBrowser() {

        if (browser == null) {
            throw new IllegalStateException(
                    "Browser is not connected."
            );
        }

        return browser;
    }

    public BrowserContext getDefaultContext() {

        if (browser.contexts().isEmpty()) {

            throw new IllegalStateException(
                    "No browser context available."
            );
        }

        return browser.contexts().get(0);
    }

    public Page getOrCreateDebugPage() {

        BrowserContext context = getDefaultContext();

        if (!context.pages().isEmpty()) {

            return context.pages().get(0);
        }

        return context.newPage();
    }

    @PreDestroy
    public void shutdown() {

        System.out.println(
                "Disconnecting Playwright..."
        );

        /*
         * IMPORTANT:
         *
         * When using connectOverCDP(), we don't want
         * BrowserPilot to own the Chromium process.
         *
         * Therefore we do NOT call browser.close().
         */

        browser = null;

        if (playwright != null) {

            try {
                playwright.close();
            } catch (Exception e) {

                System.err.println(
                        "Error while closing Playwright: "
                                + e.getMessage()
                );
            }

            playwright = null;
        }

        System.out.println(
                "Playwright disconnected."
        );
    }
}