package com.achhecode.browser_pilot.website.linkedin.page.games.patches;

import java.util.List;

public record Patch(
        List<Integer> position,        // [x, y]
        List<PatchesCommand> move      // "LEFT", "DOWN", ...
) {}