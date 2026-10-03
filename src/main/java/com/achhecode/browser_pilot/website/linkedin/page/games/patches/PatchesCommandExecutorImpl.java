package com.achhecode.browser_pilot.website.linkedin.page.games.patches;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import com.achhecode.browser_pilot.grid.GridDirection;
import com.achhecode.browser_pilot.grid.GridNavigator;
import com.achhecode.browser_pilot.grid.GridPosition;
import com.achhecode.browser_pilot.keyboard.KeyboardService;
import com.achhecode.browser_pilot.screen.MouseService;

import java.util.List;

@Slf4j
@Component
public class PatchesCommandExecutorImpl
                implements PatchesCommandExecutor {

        private final KeyboardService keyboard;
        private final GridNavigator navigator;
        private final PatchesValidator validator;
        private final PatchesMapper mapper;
        private final PatchRouteOptimizerFactory optimizerFactory;
        private final MouseService mouseService;

        public PatchesCommandExecutorImpl(
                        KeyboardService keyboard,
                        GridNavigator navigator,
                        PatchesValidator validator,
                        PatchesMapper mapper,
                        PatchRouteOptimizerFactory optimizerFactory,
                        MouseService mouseService
                ) {
                this.keyboard = keyboard;
                this.navigator = navigator;
                this.validator = validator;
                this.mapper = mapper;
                this.optimizerFactory = optimizerFactory;
                this.mouseService = mouseService;
        }

        @Override
        public synchronized void execute(
                        PatchesRequest request,
                        String executionId) {
                long start = System.nanoTime();

                try {
                        prepare(request.onlyKey());
                        
                        validator.validate(request);

                        List<PatchPath> patches = request.patches()
                                        .stream()
                                        .map(mapper::map)
                                        .toList();

                        PatchRouteStrategy strategy = request.routeStrategy() == null
                                        ? PatchRouteStrategy.INPUT_ORDER
                                        : request.routeStrategy();

                        PatchRouteOptimizer optimizer = optimizerFactory.get(strategy);

                        List<PatchPath> route = optimizer.optimize(
                                        new GridPosition(0, 0),
                                        patches);

                        executeRoute(route);

                        log.info(
                                        "Patches completed. executionId={}, patches={}, strategy={}, totalMs={}",
                                        executionId,
                                        route.size(),
                                        strategy,
                                        (System.nanoTime() - start) / 1_000_000);

                } catch (Exception e) {
                        log.error(
                                        "Patches failed. executionId={}",
                                        executionId,
                                        e);

                        throw new PatchesCommandExecutionException(
                                        "Patches keyboard automation failed",
                                        executionId,
                                        e);
                }
        }

        private void executeRoute(
                        List<PatchPath> route) {
                GridPosition current = new GridPosition(0, 0);

                for (PatchPath patch : route) {

                        current = navigator.moveTo(
                                        current,
                                        patch.start());

                        keyboard.pressSpace();

                        for (GridDirection direction : patch.moves()) {
                                current = navigator.move(
                                                current,
                                                direction);
                        }

                        keyboard.pressSpace();
                }
        }


        private void prepare(boolean onlyKey) {

                if(onlyKey) keyboard.switchTab();
                // click in empty area
                mouseService.click(350, 450);

                keyboard.addDelay(100);
                keyboard.pressTab();
                keyboard.addDelay(100);
                keyboard.pressEnter();
                keyboard.addDelay(100);
        }
}