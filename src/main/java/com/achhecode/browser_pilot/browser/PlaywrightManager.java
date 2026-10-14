package com.achhecode.browser_pilot.browser;

import com.achhecode.browser_pilot.config.BrowserPilotProperties;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.PlaywrightException;
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

    public PlaywrightManager(BrowserPilotProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    public synchronized void start() {
        playwright = Playwright.create();

        switch (properties.getBrowser().getMode()) {
            case LAUNCH -> launchBrowser();
            case ATTACH -> attachToBrowser();
            default -> throw new IllegalStateException(
                    "Unsupported browser mode: "
                            + properties.getBrowser().getMode()
            );
        }
    }

    private void launchBrowser() {
        BrowserPilotProperties.Browser config =
                properties.getBrowser();

        BrowserType.LaunchOptions options =
                new BrowserType.LaunchOptions()
                        .setHeadless(config.isHeadless());

        String executablePath = config.getExecutablePath();

        if (executablePath != null && !executablePath.isBlank()) {
            Path path = Path.of(executablePath);

            if (!Files.isRegularFile(path)) {
                throw new IllegalArgumentException(
                        "Chromium executable does not exist: "
                                + executablePath
                );
            }

            options.setExecutablePath(path);

            System.out.println(
                    "Launching custom browser: " + executablePath
            );
        } else {
            System.out.println(
                    "Launching Playwright-managed Chromium."
            );
        }

        browser = playwright.chromium().launch(options);
        context = browser.newContext();

        System.out.println("Browser launched successfully.");
    }

    private void attachToBrowser() {
        BrowserPilotProperties.Attach attach =
                properties.getBrowser().getAttach();

        String endpoint = "http://"
                + attach.getHost()
                + ":"
                + attach.getPort();

        System.out.println(
                "Connecting to existing Chromium: " + endpoint
        );

        browser = playwright.chromium().connectOverCDP(endpoint);
        context = findAttachedContext();

        System.out.println("Connected to existing Chromium.");
    }

    private BrowserContext findAttachedContext() {
        for (BrowserContext candidate : browser.contexts()) {
            try {
                // Accessing pages checks whether the context is usable.
                candidate.pages();
                return candidate;
            } catch (PlaywrightException ignored) {
                // Try another available context.
            }
        }

        throw new IllegalStateException(
                "Connected to Chromium, but no usable browser "
                        + "context exists. Ensure Chromium is running "
                        + "with its remote debugging port enabled."
        );
    }

    /**
     * Returns a usable context or recovers it when possible.
     */
    public synchronized BrowserContext getOrCreateContext() {
        ensurePlaywright();

        if (isBrowserConnected()) {
            if (isContextUsable(context)) {
                return context;
            }

            return recoverContext();
        }

        return recoverBrowser();
    }

    private BrowserContext recoverContext() {
        return switch (properties.getBrowser().getMode()) {
            case LAUNCH -> {
                context = browser.newContext();
                yield context;
            }
            case ATTACH -> {
                // Reuse the external browser's existing context.
                context = findAttachedContext();
                yield context;
            }
            default -> throw new IllegalStateException(
                    "Unsupported browser mode: "
                            + properties.getBrowser().getMode()
            );
        };
    }

    private BrowserContext recoverBrowser() {
        System.out.println(
                "Browser disconnected. Recovering browser connection..."
        );

        return switch (properties.getBrowser().getMode()) {
            case LAUNCH -> {
                launchBrowser();
                yield context;
            }
            case ATTACH -> {
                attachToBrowser();
                yield context;
            }
            default -> throw new IllegalStateException(
                    "Unsupported browser mode: "
                            + properties.getBrowser().getMode()
            );
        };
    }

    private boolean isBrowserConnected() {
        if (browser == null) {
            return false;
        }

        try {
            return browser.isConnected();
        } catch (PlaywrightException e) {
            return false;
        }
    }

    private boolean isContextUsable(BrowserContext candidate) {
        if (candidate == null) {
            return false;
        }

        try {
            candidate.pages();
            return true;
        } catch (PlaywrightException e) {
            return false;
        }
    }

    private void ensurePlaywright() {
        if (playwright == null) {
            playwright = Playwright.create();
        }
    }

    public synchronized Browser getBrowser() {
        getOrCreateContext();
        return browser;
    }

    public synchronized BrowserContext getDefaultContext() {
        return getOrCreateContext();
    }

    public synchronized BrowserContext getContext() {
        return getOrCreateContext();
    }

    @PreDestroy
    public synchronized void shutdown() {
        System.out.println("Shutting down BrowserPilot...");

        BrowserPilotProperties.Mode mode =
                properties.getBrowser().getMode();

        // Only close the browser when BrowserPilot owns it.
        if (mode == BrowserPilotProperties.Mode.LAUNCH
                && browser != null
                && browser.isConnected()) {
            try {
                browser.close();
            } catch (Exception e) {
                System.err.println(
                        "Error while closing browser: " + e.getMessage()
                );
            }
        }

        // ATTACH mode leaves the external browser running.
        browser = null;
        context = null;

        if (playwright != null) {
            try {
                playwright.close();
            } catch (Exception e) {
                System.err.println(
                        "Error while closing Playwright: " + e.getMessage()
                );
            }

            playwright = null;
        }

        System.out.println("BrowserPilot shutdown complete.");
    }
}