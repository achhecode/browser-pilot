package com.achhecode.browser_pilot.browser;

import com.achhecode.browser_pilot.config.BrowserPilotProperties;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
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

    private BrowserContext context;

    public PlaywrightManager(
            BrowserPilotProperties properties
    ) {
        this.properties = properties;
    }

    @PostConstruct
    public void start() {

        playwright = Playwright.create();

        if (properties.getBrowser().getMode()
                == BrowserPilotProperties.Mode.ATTACH) {

            attachToBrowser();

        } else {

            launchBrowser();
        }
    }

    private void launchBrowser() {

        System.out.println(
                "Starting Playwright-managed Chromium..."
        );

        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions()
                        .setHeadless(
                                properties.getBrowser().isHeadless()
                        )
        );

        context = browser.newContext();

        System.out.println(
                "Started Playwright-managed Chromium."
        );

        System.out.println(
                "Headless: "
                        + properties.getBrowser().isHeadless()
        );
    }

    private void attachToBrowser() {

        String host =
                properties.getBrowser()
                        .getAttach()
                        .getHost();

        int port =
                properties.getBrowser()
                        .getAttach()
                        .getPort();

        String endpoint =
                "http://" + host + ":" + port;

        System.out.println(
                "Connecting to existing Chromium at "
                        + endpoint
        );

        browser = playwright.chromium()
                .connectOverCDP(endpoint);

        context = getExistingContext();

        System.out.println(
                "Connected to existing Chromium."
        );
    }

    private BrowserContext getExistingContext() {

        if (browser.contexts().isEmpty()) {

            throw new IllegalStateException(
                    "Connected to Chromium, but no browser context exists."
            );
        }

        return browser.contexts().get(0);
    }

    public Browser getBrowser() {

        if (browser == null) {

            throw new IllegalStateException(
                    "Browser is not available."
            );
        }

        return browser;
    }

    public BrowserContext getContext() {

        if (context == null) {

            throw new IllegalStateException(
                    "Browser context is not available."
            );
        }

        return context;
    }

    public Page getOrCreatePage() {

        if (!context.pages().isEmpty()) {

            return context.pages().get(0);
        }

        return context.newPage();
    }

    @PreDestroy
    public void shutdown() {

        System.out.println(
                "Shutting down BrowserPilot..."
        );

        /*
         * When ATTACH mode is used, BrowserPilot
         * does not own the Chromium process.
         *
         * Therefore do not call browser.close().
         */

        if (properties.getBrowser().getMode()
                == BrowserPilotProperties.Mode.LAUNCH) {

            if (browser != null && browser.isConnected()) {

                browser.close();
            }
        }

        context = null;
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
                "BrowserPilot shutdown complete."
        );
    }
}