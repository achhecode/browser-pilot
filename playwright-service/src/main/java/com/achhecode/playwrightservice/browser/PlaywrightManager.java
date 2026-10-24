package com.achhecode.playwrightservice.browser;

import com.achhecode.playwrightservice.config.BrowserPilotProperties;
import com.achhecode.playwrightservice.exception.BrowserLifecycleException;
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
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

@Component
public class PlaywrightManager {

    private final BrowserPilotProperties properties;
    private final AtomicReference<Thread> workerThread = new AtomicReference<>();

    private final ExecutorService executor = Executors.newSingleThreadExecutor(runnable -> {
        Thread thread = new Thread(runnable, "playwright-worker");
        thread.setDaemon(false);
        workerThread.set(thread);
        return thread;
    });

    // Access these fields only on the Playwright worker thread.
    private Playwright playwright;
    private Browser browser;
    private BrowserContext context;

    private volatile boolean started;
    private volatile boolean shuttingDown;

    public PlaywrightManager(BrowserPilotProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    public void start() {
        execute(() -> {
            initializeRuntime();
            ensureContext();
            started = true;
            return null;
        });
    }

    /**
     * Executes a Playwright operation against the current usable browser context on
     * the dedicated Playwright thread.
     *
     * Do not retain the context or its Page objects for use outside the supplied
     * operation.
     */
    public <T> T executeInContext(Function<BrowserContext, T> operation) {
        if (operation == null) {
            throw new IllegalArgumentException("Operation must not be null.");
        }

        return execute(() -> {
            if (!started) {
                throw new BrowserLifecycleException("PlaywrightManager is not started.");
            }

            BrowserContext usableContext = ensureContext();

            return operation.apply(usableContext);
        });
    }

    private void initializeRuntime() {
        if (playwright != null) {
            return;
        }

        playwright = Playwright.create();
    }

    private BrowserContext ensureContext() {
        initializeRuntime();

        if (!isBrowserConnected()) {
            reconnectBrowser();
        }

        if (isContextUsable(context)) {
            return context;
        }

        return recoverContext();
    }

    private BrowserContext recoverContext() {
        return switch (properties.getBrowser().getMode()) {
        case LAUNCH -> {
            try {
                context = browser.newContext();
                yield context;
            } catch (PlaywrightException exception) {
                reconnectBrowser();
                yield context;
            }
        }

        case ATTACH -> {
            context = findAttachedContext();

            if (context == null) {
                throw new BrowserLifecycleException(
                        "No usable browser context is available " + "in ATTACH mode. Ensure Chromium "
                                + "is running and its debugging " + "endpoint is accessible.");
            }

            yield context;
        }
        };
    }

    private void reconnectBrowser() {
        closeRuntime();

        initializeRuntime();

        switch (properties.getBrowser().getMode()) {
        case LAUNCH -> launchBrowser();
        case ATTACH -> attachToBrowser();
        }

        if (context == null) {
            throw new BrowserLifecycleException("Browser recovery completed without a context.");
        }
    }

    private void launchBrowser() {
        BrowserPilotProperties.Browser config = properties.getBrowser();

        BrowserType.LaunchOptions options = new BrowserType.LaunchOptions().setHeadless(config.isHeadless());

        String executablePath = config.getExecutablePath();

        if (executablePath != null && !executablePath.isBlank()) {

            Path path = Path.of(executablePath).toAbsolutePath().normalize();

            if (!Files.isRegularFile(path)) {
                throw new BrowserLifecycleException("Browser executable does not exist: " + path);
            }

            options.setExecutablePath(path);
        }

        browser = playwright.chromium().launch(options);
        context = browser.newContext();
    }

    private void attachToBrowser() {
        BrowserPilotProperties.Attach attach = properties.getBrowser().getAttach();

        String endpoint = "http://" + attach.getHost() + ":" + attach.getPort();

        try {
            browser = playwright.chromium().connectOverCDP(endpoint);

            context = findAttachedContext();

            if (context == null) {
                throw new BrowserLifecycleException(
                        "Connected to Chromium, but no usable " + "browser context was found.");
            }
        } catch (PlaywrightException exception) {
            browser = null;
            context = null;

            throw new BrowserLifecycleException("Could not connect to Chromium at " + endpoint
                    + ". Check that remote debugging " + "is enabled and reachable.", exception);
        }
    }

    private BrowserContext findAttachedContext() {
        if (browser == null || !isBrowserConnected()) {
            return null;
        }

        try {
            for (BrowserContext candidate : browser.contexts()) {
                try {
                    candidate.pages();
                    return candidate;
                } catch (PlaywrightException ignored) {
                    // Try the next context.
                }
            }
        } catch (PlaywrightException ignored) {
            return null;
        }

        return null;
    }

    private boolean isBrowserConnected() {
        if (browser == null) {
            return false;
        }

        try {
            return browser.isConnected();
        } catch (PlaywrightException exception) {
            return false;
        }
    }

    private boolean isContextUsable(BrowserContext candidate) {
        if (candidate == null || !isBrowserConnected()) {
            return false;
        }

        try {
            candidate.pages();
            return true;
        } catch (PlaywrightException exception) {
            return false;
        }
    }

    private void closeRuntime() {
        // All cleanup occurs on the Playwright worker thread.
        context = null;
        browser = null;

        if (playwright != null) {
            try {
                playwright.close();
            } catch (Exception ignored) {
                // Continue recovery even if cleanup fails.
            } finally {
                playwright = null;
            }
        }
    }

    private <T> T execute(Callable<T> operation) {
        if (shuttingDown) {
            throw new BrowserLifecycleException("PlaywrightManager is shutting down.");
        }

        // Avoid deadlock when called from the Playwright worker thread itself.
        if (Thread.currentThread() == workerThread.get()) {
            try {
                return operation.call();
            } catch (RuntimeException exception) {
                throw exception;
            } catch (Exception exception) {
                throw new BrowserLifecycleException("Playwright operation failed.", exception);
            }
        }

        Future<T> future;

        try {
            future = executor.submit(operation);
        } catch (RuntimeException exception) {
            throw new BrowserLifecycleException("Could not schedule a Playwright operation.", exception);
        }

        try {
            return future.get();
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new BrowserLifecycleException("Interrupted while waiting for Playwright.", exception);
        } catch (ExecutionException exception) {
            Throwable cause = exception.getCause();

            if (cause instanceof RuntimeException runtimeException) {
                throw runtimeException;
            }

            throw new BrowserLifecycleException("Playwright operation failed.", cause);
        }
    }

    
    @PreDestroy
    public void shutdown() {
        shuttingDown = true;

        try {
            Future<?> cleanup = executor.submit(() -> {
                BrowserPilotProperties.Mode mode = properties.getBrowser().getMode();

                // Close only a browser owned by this application.
                if (mode == BrowserPilotProperties.Mode.LAUNCH && browser != null && isBrowserConnected()) {
                    try {
                        browser.close();
                    } catch (Exception ignored) {
                        // Continue cleanup.
                    }
                }

                closeRuntime();
            });

            cleanup.get(10, TimeUnit.SECONDS);
        } catch (Exception exception) {
            // Best-effort bounded shutdown.
        } finally {
            executor.shutdownNow();
        }
    }
}