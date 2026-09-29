package com.achhecode.browser_pilot.keyboard.typing;

public record TextTypingRequest(
        String text,
        TextTypingMode mode
) {
}