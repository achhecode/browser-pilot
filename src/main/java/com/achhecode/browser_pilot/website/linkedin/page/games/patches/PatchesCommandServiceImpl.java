package com.achhecode.browser_pilot.website.linkedin.page.games.patches;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PatchesCommandServiceImpl
                implements PatchesCommandService {

        private final PatchesCommandExecutor commandExecutor;

        public PatchesCommandServiceImpl(PatchesCommandExecutor commandExecutor) {
                this.commandExecutor = commandExecutor;
        }

        @Override
        public void executeCommand(PatchesRequest patchesRequest, String executionId) {
                long start = System.currentTimeMillis();

                commandExecutor.execute(patchesRequest, executionId);

                log.info("Patches automation completed. executionId={}, durationMs={}",
                                executionId, System.currentTimeMillis() - start);
                return;
        }
}