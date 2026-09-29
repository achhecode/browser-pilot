package com.achhecode.browser_pilot.website.linkedin.page.games.patches;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class PatchesCommandServiceImpl
                implements PatchesCommandService {

        private final PatchesCommandParser parser;
        private final PatchesCommandExecutor commandExecutor;

        public PatchesCommandServiceImpl(PatchesCommandParser parser, PatchesCommandExecutor commandExecutor) {
                this.parser = parser;
                this.commandExecutor = commandExecutor;
        }

        @Override
        public int executeCommand(PatchesRequest input, String executionId) {
                long start = System.currentTimeMillis();

                List<PatchesRequest.Patch> patches = parser.parse(input);
                commandExecutor.execute(patches, input.gridWidth(), input.gridHeight(), executionId);

                log.info("Patches automation completed. executionId={}, patchCount={}, durationMs={}",
                                executionId, patches.size(), System.currentTimeMillis() - start);
                return patches.size();
        }
}