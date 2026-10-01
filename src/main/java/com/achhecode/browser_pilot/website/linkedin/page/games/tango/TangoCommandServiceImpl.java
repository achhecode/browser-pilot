package com.achhecode.browser_pilot.website.linkedin.page.games.tango;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class TangoCommandServiceImpl
        implements TangoCommandService {

    private final TangoCommandExecutor commandExecutor;

    public TangoCommandServiceImpl(
            TangoCommandExecutor commandExecutor
    ) {
        this.commandExecutor = commandExecutor;
    }

    @Override
    public int executeCommand(
            TangoRequest tangoRequest,
            String executionId
    ) {

        try {

            long startTime = System.currentTimeMillis();
            commandExecutor.execute(
                    tangoRequest,
                    executionId
            );
            
            long duration =
                    System.currentTimeMillis() - startTime;

            log.info(
                    "N-Queen automation completed. " +
                    "executionId={}, n={}, durationMs={}",
                    executionId,
                    tangoRequest.instructions().length(),
                    duration
            );

            return tangoRequest.instructions().length();

        } catch (Exception e) {

            log.error(
                    "Tango automation failed. executionId={}",
                    executionId,
                    e
            );

            throw e;
        }
    }
}