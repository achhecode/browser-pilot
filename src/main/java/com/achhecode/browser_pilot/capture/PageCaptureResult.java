package com.achhecode.browser_pilot.capture;

public record PageCaptureResult(
        String directory,
        String url,
        String title
) {}