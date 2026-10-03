package com.achhecode.browser_pilot.website.linkedin.page.games.crossclimb;

import jakarta.validation.constraints.NotBlank;

public record CrossclimbClue(
        @NotBlank
        String clue,

        @NotBlank
        String answer
) {
}