package com.achhecode.playwrightservice.browser;

import java.time.Instant;

public record TabResponse(String id, int index, String url, String title, Instant createdAt, boolean closed) {
}
