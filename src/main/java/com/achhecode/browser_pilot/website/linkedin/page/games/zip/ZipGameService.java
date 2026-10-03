package com.achhecode.browser_pilot.website.linkedin.page.games.zip;

import org.springframework.stereotype.Service;

import com.achhecode.browser_pilot.website.linkedin.page.games.LinkedInGameSolver;

@Service
public class ZipGameService implements LinkedInGameSolver<ZipRequest, ZipPage> {

    private final ZipCommandService commandService;

    public ZipGameService(
            ZipCommandService commandService
    ) {
        this.commandService = commandService;
    }

    @Override 
    public void solve(
            ZipPage zipPage,
            ZipRequest request
    ) {
        zipPage.waitUntilReady();

        // later execute using playwright only
        
        commandService.execute(
                request
        );
    }

    @Override 
    public void solve(
            ZipRequest request
    ) {
        commandService.execute(
                request
        );
    }
}