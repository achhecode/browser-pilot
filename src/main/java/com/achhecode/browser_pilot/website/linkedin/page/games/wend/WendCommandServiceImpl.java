package com.achhecode.browser_pilot.website.linkedin.page.games.wend;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class WendCommandServiceImpl
                implements WendCommandService {

        private final WendCommandExecutor commandExecutor;

        public WendCommandServiceImpl(WendCommandExecutor commandExecutor) {
                this.commandExecutor = commandExecutor;
        }

        @Override
        public void executeCommand(WendRequest wendRequest, String executionId) {
                long start = System.currentTimeMillis();

                commandExecutor.execute(wendRequest, executionId);

                log.info("Wend automation completed. executionId={}, durationMs={}",
                                executionId, System.currentTimeMillis() - start);
                return;
        }
}