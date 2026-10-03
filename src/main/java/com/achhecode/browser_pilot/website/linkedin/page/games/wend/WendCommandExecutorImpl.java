package com.achhecode.browser_pilot.website.linkedin.page.games.wend;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import com.achhecode.browser_pilot.grid.GridDirection;
import com.achhecode.browser_pilot.grid.GridNavigator;
import com.achhecode.browser_pilot.grid.GridPosition;
import com.achhecode.browser_pilot.keyboard.KeyboardService;

import java.util.List;

@Slf4j
@Component
public class WendCommandExecutorImpl
                implements WendCommandExecutor {

        private final KeyboardService keyboard;
        private final GridNavigator navigator;
        private final WendValidator validator;
        private final WendMapper mapper;
        private final WendRouteOptimizerFactory optimizerFactory;

        public WendCommandExecutorImpl(
                        KeyboardService keyboard,
                        GridNavigator navigator,
                        WendValidator validator,
                        WendMapper mapper,
                        WendRouteOptimizerFactory optimizerFactory) {
                this.keyboard = keyboard;
                this.navigator = navigator;
                this.validator = validator;
                this.mapper = mapper;
                this.optimizerFactory = optimizerFactory;
        }

        @Override
        public synchronized void execute(
                        WendRequest request,
                        String executionId) {
                long start = System.nanoTime();

                try {
                        if(request.onlyKey()){
                                keyboard.switchTab();
                        }
                        
                        validator.validate(request);

                        List<WendPath> wend = request.wend()
                                        .stream()
                                        .map(mapper::map)
                                        .toList();

                        WendRouteStrategy strategy = request.routeStrategy() == null
                                        ? WendRouteStrategy.INPUT_ORDER
                                        : request.routeStrategy();

                        WendRouteOptimizer optimizer = optimizerFactory.get(strategy);

                        List<WendPath> route = optimizer.optimize(
                                        new GridPosition(0, 0),
                                        wend);

                        executeRoute(route);

                        log.info(
                                        "Wend completed. executionId={}, wend={}, strategy={}, totalMs={}",
                                        executionId,
                                        route.size(),
                                        strategy,
                                        (System.nanoTime() - start) / 1_000_000);

                } catch (Exception e) {
                        log.error(
                                        "Wend failed. executionId={}",
                                        executionId,
                                        e);

                        throw new WendCommandExecutionException(
                                        "Wend keyboard automation failed",
                                        executionId,
                                        e);
                }
        }

        private void executeRoute(
                        List<WendPath> route) {
                GridPosition current = new GridPosition(0, 0);

                for (WendPath patch : route) {

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
}