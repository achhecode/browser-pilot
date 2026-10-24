package com.achhecode.playwrightservice.browser.dto;

import java.time.Instant;

public record TabResponse(String id, String url, String title, Instant createdAt, boolean closed) {
}