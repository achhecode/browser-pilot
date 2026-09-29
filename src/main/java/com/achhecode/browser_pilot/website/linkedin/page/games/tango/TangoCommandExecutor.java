package com.achhecode.browser_pilot.website.linkedin.page.games.tango;

import java.util.List;

public interface TangoCommandExecutor {

    void execute(
            List<TangoCommand> commands,
            String executionId
    );
}