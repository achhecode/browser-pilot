package com.achhecode.browser_pilot.website.linkedin.page.games.patches;

import java.util.List;

public interface PatchesCommandGenerator {
    List<PatchesCommand> generate(List<PatchesRequest.Patch> patches);
}