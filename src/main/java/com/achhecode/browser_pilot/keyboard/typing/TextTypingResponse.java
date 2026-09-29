package com.achhecode.browser_pilot.keyboard.typing;

public record TextTypingResponse(

        String status,

        String executionId,

        int characterCount,

        long executionTimeMs,

        TextTypingMode mode

) {
}