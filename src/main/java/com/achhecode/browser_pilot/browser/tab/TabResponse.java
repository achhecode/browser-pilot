package com.achhecode.browser_pilot.browser.tab;

public record TabResponse (
    String id,
    int index, 
    String url, 
    String title, 
    boolean closed
) {
}
