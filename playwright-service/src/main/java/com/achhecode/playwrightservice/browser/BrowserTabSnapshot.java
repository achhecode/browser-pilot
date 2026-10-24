package com.achhecode.playwrightservice.browser;

import java.time.Instant;

public record BrowserTabSnapshot(
        String id,
        String url,
        String title,
        Instant createdAt,
        boolean closed
) {
}