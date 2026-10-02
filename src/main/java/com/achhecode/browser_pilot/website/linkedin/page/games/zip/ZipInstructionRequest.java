package com.achhecode.browser_pilot.website.linkedin.page.games.zip;

import jakarta.validation.constraints.NotBlank;

public record ZipInstructionRequest(
        @NotBlank(message = "instruction is required")
        String instruction
) {
}