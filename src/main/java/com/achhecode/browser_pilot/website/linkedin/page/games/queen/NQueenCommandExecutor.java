package com.achhecode.browser_pilot.website.linkedin.page.games.queen;

public interface NQueenCommandExecutor {

    void execute(
            QueensRequest queensRequest,
            String executionId
    );
}