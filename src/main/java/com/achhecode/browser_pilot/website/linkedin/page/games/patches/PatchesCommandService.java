package com.achhecode.browser_pilot.website.linkedin.page.games.patches;

public interface PatchesCommandService {

    /** Returns the number of patches executed. */
    void executeCommand(PatchesRequest input, String executionId);
}