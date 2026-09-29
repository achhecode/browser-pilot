package com.achhecode.browser_pilot.website.linkedin.page.games.patches;

import java.util.List;

public interface PatchesCommandExecutor {

    void execute(List<PatchesCommandInput.Patch> patches, Integer gridWidth, Integer gridHeight, String executionId);
}