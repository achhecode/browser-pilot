package com.achhecode.browser_pilot.website.linkedin.page.games.tango;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.achhecode.browser_pilot.grid.GridPosition;
import com.achhecode.browser_pilot.grid.GridTraversal;
import com.achhecode.browser_pilot.keyboard.KeyboardService;
import com.achhecode.browser_pilot.keyboard.SnakeGridTraversal;
import com.achhecode.browser_pilot.keyboard.SnakeTraversalStrategy;
import com.achhecode.browser_pilot.screen.MouseService;
import com.achhecode.browser_pilot.website.linkedin.LinkedInGame;

import lombok.extern.slf4j.Slf4j;

@Slf4j 
@Component 
public class TangoCommandExecutorImpl
        implements TangoCommandExecutor {

    private final KeyboardService keyboard;
    private final GridTraversal gridTraversal;
    private final MouseService mouseService;

    public TangoCommandExecutorImpl(
            KeyboardService keyboard,
            GridTraversal gridTraversal,
            MouseService mouseService
    ) {
        this.keyboard = keyboard;
        this.gridTraversal = gridTraversal;
        this.mouseService = mouseService;
    }

    @Override
    public synchronized void execute(
            TangoRequest request,
            String executionId
    ) {
        try {

            prepare(request.onlyKey());
            
            TangoPositions positions =
                    TangoPositionMapper.from(
                            request.instructions()
                    );

            List<GridPosition> traversal =
                    SnakeGridTraversal.create(
                            positions.gridSize(),
                            SnakeTraversalStrategy.LEFT_TO_RIGHT_SNAKE
                    );

            executeTraversal(
                    traversal,
                    positions.suns(),
                    positions.moons(),
                    request.keySpeed()
            );

            log.info(
                    "Tango completed. executionId={}, gridSize={}",
                    executionId,
                    positions.gridSize()
            );

        } catch (Exception e) {
            throw new TangoCommandExecutionException(
                    "Tango keyboard automation failed",
                    executionId,
                    e
            );
        }
    }

    private void pressSun(GridPosition position) {
        keyboard.pressSpace();

        log.info(
                "Pressed space once for SUN at {}",
                position
        );
    }

    private void pressMoon(GridPosition position) {
        keyboard.pressSpace();
        keyboard.pressSpace();

        log.info(
                "Pressed space twice for MOON at {}",
                position
        );
    }

    private void executeTraversal(
        List<GridPosition> traversal,
        Set<GridPosition> suns,
        Set<GridPosition> moons,
        int keySpeed
    ) {
        log.info(
                "Traversal={}, suns={}, moons={}",
                traversal,
                suns,
                moons
        );

        for (int index = 0; index < traversal.size(); index++) {

            GridPosition current = traversal.get(index);

            if (suns.contains(current)) {
                pressSun(current);
            } else if (moons.contains(current)) {
                pressMoon(current);
            }

            if (index + 1 < traversal.size()) {

                gridTraversal.move(
                        current,
                        traversal.get(index + 1)
                );

                keyboard.addDelay(keySpeed);
            }
        }
    }


    private void prepare(boolean onlyKey) {

            if(onlyKey) keyboard.switchTab();
            // click in empty area
            mouseService.click(LinkedInGame.TANGO.location().x(), LinkedInGame.TANGO.location().y());

            keyboard.addDelay(100);
            keyboard.pressTab();
            keyboard.addDelay(100);
            keyboard.pressEnter();
            keyboard.addDelay(100);
    }
}