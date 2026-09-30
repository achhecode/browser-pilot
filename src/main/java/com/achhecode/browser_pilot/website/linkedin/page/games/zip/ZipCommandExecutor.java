package com.achhecode.browser_pilot.website.linkedin.page.games.zip;

import java.util.List;

import com.achhecode.browser_pilot.keyboard.ArrowDirection;


public interface ZipCommandExecutor {
    void execute(
        List<ArrowDirection> commands,
        boolean switchTab,
        String executionId
    );
}