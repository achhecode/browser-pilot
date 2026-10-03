package com.achhecode.browser_pilot.website.linkedin.page.games.pinpoint;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.achhecode.browser_pilot.website.linkedin.page.games.LinkedInGameSolver;

@Service
public class PinpointGameService  implements LinkedInGameSolver<PinpointRequest, PinpointPage> {

    private final PinpointCommandExecutor commandExecutor;

    public PinpointGameService(
            PinpointCommandExecutor commandExecutor
    ) {
        this.commandExecutor = commandExecutor;
    }

    @Override 
    public void solve(
            PinpointPage page,
            PinpointRequest request
    ) {
        String executionId =
                UUID.randomUUID().toString();

        page.waitUntilReady();

        // later execute using playwright only
        
        commandExecutor.execute(
                request,
                executionId
        );
    }
    
    @Override 
    public void solve(
            PinpointRequest request
    ) {
        String executionId =
                UUID.randomUUID().toString();


        commandExecutor.execute(
                request,
                executionId
        );
    }
}