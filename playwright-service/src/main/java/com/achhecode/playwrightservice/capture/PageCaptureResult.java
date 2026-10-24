package com.achhecode.playwrightservice.capture;

public record PageCaptureResult(
        String directory,
        String url,
        String title
) {}