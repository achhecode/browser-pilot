package com.achhecode.browser_pilot.website.linkedin.page.games.queen;


import lombok.extern.slf4j.Slf4j;

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

@Slf4j
@Component
public class QueensCommandExecutorImpl
        implements QueensCommandExecutor {

    private final KeyboardService keyboard;
    private final GridTraversal gridTraversal;
    private final MouseService mouseService;

    public QueensCommandExecutorImpl(
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
            QueensRequest request,
            String executionId
    ) {

        try {

            prepare(request.onlyKey());
            
            Set<GridPosition> queens =
                    QueensPositionMapper.from(
                            request.positions()
                    );

            List<GridPosition> traversal =
                    SnakeGridTraversal.create(
                            request.positions().size(),
                            SnakeTraversalStrategy.LEFT_TO_RIGHT_SNAKE
                    );

            executeTraversal(
                    traversal,
                    queens,
                    request.keySpeed()
            );

            log.info(
                    "N-Queen completed. executionId={}",
                    executionId
            );

        } catch (Exception e) {
            throw new QueensCommandExecutionException(
                    "N-Queen keyboard automation failed",
                    executionId,
                    e
            );
        }
    }

    private void pressQueen(GridPosition position) {
        keyboard.pressSpace();
        keyboard.pressSpace();

        log.info(
                "Pressed space twice at grid position: {}",position
        );
    }

    private void executeTraversal(
        List<GridPosition> traversal,
        Set<GridPosition> queens,
        int keySpeed
    ) {

        log.info("Traversal {}, queens {}", traversal, queens);

        for (int index = 0; index < traversal.size(); index++) {

            GridPosition current = traversal.get(index);

            if (queens.contains(current)) {
                pressQueen(current);
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

        if(onlyKey){
            keyboard.switchTab();
        }else{
            keyboard.addDelay(800);
        }
        // click in empty area
        mouseService.click(LinkedInGame.QUEENS.location().x(), LinkedInGame.QUEENS.location().y());

        keyboard.addDelay(100);
        keyboard.pressTab();
        keyboard.addDelay(100);
        keyboard.pressEnter();
        keyboard.addDelay(100);
    }
}