package com.achhecode.browser_pilot.website.linkedin.page.games.wend;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import com.achhecode.browser_pilot.grid.BlockedGridLayout;
import com.achhecode.browser_pilot.grid.GridDirection;
import com.achhecode.browser_pilot.grid.GridPosition;
import com.achhecode.browser_pilot.grid.WrappingGridNavigator;
import com.achhecode.browser_pilot.keyboard.KeyboardService;
import com.achhecode.browser_pilot.screen.MouseService;
import com.achhecode.browser_pilot.website.linkedin.LinkedInGame;

import java.util.List;

@Slf4j
@Component
public class WendCommandExecutorImpl
        implements WendCommandExecutor {

    private final KeyboardService keyboard;
    private final WendValidator validator;
    private final WendMapper mapper;
    private final WendRouteOptimizerFactory optimizerFactory;
    private final MouseService mouseService;

    public WendCommandExecutorImpl(
            KeyboardService keyboard,
            WendValidator validator,
            WendMapper mapper,
            WendRouteOptimizerFactory optimizerFactory,
            MouseService mouseService) {
        this.keyboard = keyboard;
        this.validator = validator;
        this.mapper = mapper;
        this.optimizerFactory = optimizerFactory;
        this.mouseService = mouseService;
    }

    @Override
    public synchronized void execute(
            WendRequest request,
            String executionId) {
        long start = System.nanoTime();

        try {
                BlockedGridLayout layout = new BlockedGridLayout(
                        request.gridHeight(),
                        request.gridWidth(),
                        request.blocked().stream()
                                .map(b -> new GridPosition(b.get(0), b.get(1)))
                                .toList());

                validator.validate(request, layout);
                prepare(request.onlyKey());

                WendRouteStrategy strategy = request.routeStrategy() == null
                        ? WendRouteStrategy.INPUT_ORDER
                        : request.routeStrategy();

                WrappingGridNavigator navigator = new WrappingGridNavigator(keyboard, layout);
                
            List<WendPath> wend = request.wend().stream()
                    .map(mapper::map)
                    .toList();

            List<WendPath> route = optimizerFactory.get(strategy)
                    .optimize(navigator.start(), wend);

            executeRoute(navigator, route);

            log.info(
                    "Wend completed. executionId={}, wend={}, strategy={}, totalMs={}",
                    executionId,
                    route.size(),
                    strategy,
                    (System.nanoTime() - start) / 1_000_000);

        } catch (Exception e) {
            log.error("Wend failed. executionId={}", executionId, e);

            throw new WendCommandExecutionException(
                    "Wend keyboard automation failed",
                    executionId,
                    e);
        }
    }

    private void executeRoute(WrappingGridNavigator navigator, List<WendPath> route) {
        GridPosition current = navigator.start();

        for (WendPath path : route) {
            current = navigator.moveTo(current, path.start());
            keyboard.pressSpace();

            for (GridDirection direction : path.moves()) {
                current = navigator.move(current, direction);
                keyboard.addDelay(10);

            }
            keyboard.pressSpace();
            keyboard.addDelay(50);
        }
    }

    private void prepare(boolean onlyKey) {
        if (onlyKey) {
            keyboard.switchTab();
        }else{
            keyboard.addDelay(1000); // 1 sec solution not accepted
        }

        // click in empty area
        mouseService.click(LinkedInGame.WEND.location().x(), LinkedInGame.WEND.location().y());

        keyboard.addDelay(100);
        keyboard.pressTab();
        keyboard.addDelay(100);
    }
}