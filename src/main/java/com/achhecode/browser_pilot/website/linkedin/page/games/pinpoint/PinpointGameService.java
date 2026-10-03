package com.achhecode.browser_pilot.website.linkedin.page.games.pinpoint;

import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class PinpointGameService {

    private final PinpointCommandExecutor commandExecutor;

    public PinpointGameService(
            PinpointCommandExecutor commandExecutor
    ) {
        this.commandExecutor = commandExecutor;
    }

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