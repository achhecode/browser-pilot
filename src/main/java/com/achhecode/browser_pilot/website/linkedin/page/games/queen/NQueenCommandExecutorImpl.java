package com.achhecode.browser_pilot.website.linkedin.page.games.queen;


import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.achhecode.browser_pilot.keyboard.GridPosition;
import com.achhecode.browser_pilot.keyboard.GridTraversal;
import com.achhecode.browser_pilot.keyboard.KeyboardService;
import com.achhecode.browser_pilot.keyboard.SnakeGridTraversal;
import com.achhecode.browser_pilot.keyboard.SnakeTraversalStrategy;

@Slf4j
@Component
public class NQueenCommandExecutorImpl
        implements NQueenCommandExecutor {

    private final KeyboardService keyboard;
    private final GridTraversal gridTraversal;

    public NQueenCommandExecutorImpl(
            KeyboardService keyboard,
            GridTraversal gridTraversal
    ) {
        this.keyboard = keyboard;
        this.gridTraversal = gridTraversal;
    }

    @Override
    public synchronized void execute(
            QueensRequest request,
            String executionId
    ) {

        try {

            if(request.onlyKey()){
                keyboard.switchTab();
                keyboard.addDelay(1000);
            }
            
            Set<GridPosition> queens =
                    QueenPositionMapper.from(
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
            throw new NQueenCommandExecutionException(
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
}