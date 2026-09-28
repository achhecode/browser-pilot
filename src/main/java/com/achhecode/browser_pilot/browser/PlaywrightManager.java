package com.achhecode.browser_pilot.browser;

import com.achhecode.browser_pilot.config.BrowserPilotProperties;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Playwright;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PlaywrightManager {

    private final BrowserPilotProperties properties;

    private Playwright playwright;

    private final List<Browser> browsers = new ArrayList<>();

    public PlaywrightManager(BrowserPilotProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    public void start() {

        playwright = Playwright.create();

        for (int i = 0; i < properties.getBrowserCount(); i++) {

            Browser browser = playwright.chromium().launch(
                    new BrowserType.LaunchOptions()
                            .setHeadless(properties.isHeadless())
            );

            browsers.add(browser);
        }
    }

    public Browser getBrowser(int index) {

        if (index < 0 || index >= browsers.size()) {
            throw new IllegalArgumentException(
                    "Invalid browser index: " + index
            );
        }

        return browsers.get(index);
    }

    public BrowserContext createContext() {
        return getBrowser(0).newContext();
    }

    public PageHandle createPage() {

        BrowserContext context = createContext();

        return new PageHandle(
                context,
                context.newPage()
        );
    }

    public int browserCount() {
        return browsers.size();
    }

    @PreDestroy
    public void shutdown() {

        for (Browser browser : browsers) {

            if (browser.isConnected()) {
                browser.close();
            }
        }

        browsers.clear();

        if (playwright != null) {
            playwright.close();
        }
    }
}