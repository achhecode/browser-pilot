package com.achhecode.browser_pilot.website.linkedin.page.games.tango;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class TangoCommandServiceImpl
        implements TangoCommandService {

    private final TangoCommandGenerator commandGenerator;
    private final TangoCommandExecutor commandExecutor;

    public TangoCommandServiceImpl(
            TangoCommandGenerator commandGenerator,
            TangoCommandExecutor commandExecutor
    ) {
        this.commandGenerator = commandGenerator;
        this.commandExecutor = commandExecutor;
    }

    @Override
    public int executeCommand(
            String instruction,
            String executionId
    ) {

        long startTime = System.currentTimeMillis();

        try {

            List<TangoCommand> commands =
                    commandGenerator.generate(instruction);

            commandExecutor.execute(
                    commands,
                    executionId
            );

            long duration =
                    System.currentTimeMillis() - startTime;

            log.info(
                    "Tango automation completed. " +
                    "executionId={}, commandCount={}, durationMs={}",
                    executionId,
                    commands.size(),
                    duration
            );

            return commands.size();

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