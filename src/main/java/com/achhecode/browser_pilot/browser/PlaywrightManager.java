package com.achhecode.browser_pilot.browser;

import com.achhecode.browser_pilot.config.BrowserPilotProperties;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Playwright;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;

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

        BrowserPilotProperties.Mode mode =
                properties.getBrowser().getMode();

        switch (mode) {

            case LAUNCH -> launchBrowser();

            case ATTACH -> attachToBrowser();

            default -> throw new IllegalStateException(
                    "Unsupported browser mode: " + mode
            );
        }
    }

    private void launchBrowser() {

        BrowserPilotProperties.Browser browserProperties =
                properties.getBrowser();

        BrowserType.LaunchOptions options =
                new BrowserType.LaunchOptions()
                        .setHeadless(
                                browserProperties.isHeadless()
                        );

        String executablePath =
                browserProperties.getExecutablePath();

        if (executablePath != null
                && !executablePath.isBlank()) {

            Path path = Path.of(executablePath);

            if (!Files.isRegularFile(path)) {

                throw new IllegalArgumentException(
                        "Chromium executable does not exist: "
                                + executablePath
                );
            }

            options.setExecutablePath(path);

            System.out.println(
                    "Launching custom browser: "
                            + executablePath
            );

        } else {

            System.out.println(
                    "Launching Playwright-managed Chromium."
            );
        }

        browser = playwright.chromium()
                .launch(options);

        context = browser.newContext();

        System.out.println(
                "Browser launched successfully."
        );
    }

    private void attachToBrowser() {

        BrowserPilotProperties.Attach attach =
                properties.getBrowser().getAttach();

        String endpoint =
                "http://"
                        + attach.getHost()
                        + ":"
                        + attach.getPort();

        System.out.println(
                "Connecting to existing Chromium: "
                        + endpoint
        );

        browser = playwright.chromium()
                .connectOverCDP(endpoint);

        if (browser.contexts().isEmpty()) {

            throw new IllegalStateException(
                    "Connected to Chromium, "
                            + "but no browser context exists."
            );
        }

        /*
         * For CDP attach mode we use the first
         * existing browser context.
         */
        context = browser.contexts().get(0);

        System.out.println(
                "Connected to existing Chromium."
        );
    }

    public Browser getBrowser() {

        if (browser == null) {

            throw new IllegalStateException(
                    "Browser is not available."
            );
        }

        return browser;
    }

    public BrowserContext getDefaultContext() {

        if (context == null) {

            throw new IllegalStateException(
                    "Browser context is not available."
            );
        }

        return context;
    }

    public BrowserContext getContext() {

        return getDefaultContext();
    }

    @PreDestroy
    public void shutdown() {

        System.out.println(
                "Shutting down BrowserPilot..."
        );

        BrowserPilotProperties.Mode mode =
                properties.getBrowser().getMode();

        /*
         * BrowserPilot owns the browser only when
         * it launched it.
         */
        if (mode == BrowserPilotProperties.Mode.LAUNCH) {

            if (browser != null
                    && browser.isConnected()) {

                try {

                    browser.close();

                } catch (Exception e) {

                    System.err.println(
                            "Error while closing browser: "
                                    + e.getMessage()
                    );
                }
            }
        }

        /*
         * ATTACH mode:
         *
         * Do NOT close the external browser.
         */

        browser = null;

        context = null;

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