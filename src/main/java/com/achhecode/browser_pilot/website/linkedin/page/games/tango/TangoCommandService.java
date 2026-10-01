package com.achhecode.browser_pilot.website.linkedin.page.games.tango;

public interface TangoCommandService {

    int executeCommand(
            TangoRequest tangoRequest,
            String executionId
    );
}