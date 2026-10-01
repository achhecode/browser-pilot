package com.achhecode.browser_pilot.website.linkedin.page.games.zip;

import org.springframework.stereotype.Service;

@Service
public class ZipGameService {

    private final ZipCommandService commandService;

    public ZipGameService(
            ZipCommandService commandService
    ) {
        this.commandService = commandService;
    }

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


    public void solve(
            ZipRequest request
    ) {
        commandService.execute(
                request
        );
    }
}