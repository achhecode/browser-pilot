package com.achhecode.playwrightservice.browser;

import com.microsoft.playwright.Page;

import java.time.Instant;
import java.util.Objects;

public final class BrowserTab {

    private final String id;
    private final Page page;
    private final Instant createdAt;

    public BrowserTab(String id, Page page) {
        this.id = Objects.requireNonNull(id, "id");
        this.page = Objects.requireNonNull(page, "page");
        this.createdAt = Instant.now();
    }

    public String getId() {
        return id;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    /**
     * Internal access only. Call only from a Playwright worker-thread operation.
     */
    Page page() {
        return page;
    }
}