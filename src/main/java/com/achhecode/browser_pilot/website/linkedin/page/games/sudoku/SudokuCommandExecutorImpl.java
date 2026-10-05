package com.achhecode.browser_pilot.website.linkedin.page.games.sudoku;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.achhecode.browser_pilot.grid.GridPosition;
import com.achhecode.browser_pilot.grid.GridTraversal;
import com.achhecode.browser_pilot.keyboard.KeyboardService;
import com.achhecode.browser_pilot.keyboard.SnakeGridTraversal;
import com.achhecode.browser_pilot.keyboard.SnakeTraversalStrategy;
import com.achhecode.browser_pilot.screen.MouseService;
import com.achhecode.browser_pilot.website.linkedin.LinkedInGame;
import com.achhecode.browser_pilot.website.linkedin.page.games.minisudoku.MiniSudokuRequest;
import com.achhecode.browser_pilot.website.linkedin.page.games.tango.TangoCommandExecutionException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SudokuCommandExecutorImpl
                implements SudokuCommandExecutor {

        private final KeyboardService keyboard;
        private final GridTraversal gridTraversal;
        private final MouseService mouseService;


        public SudokuCommandExecutorImpl(
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
                        MiniSudokuRequest request,
                        String executionId) {
                try {

                        prepare(request.onlyKey());

                        SudokuPositions positions = SudokuPositionMapper.from(
                                        request.instructions());

                        List<GridPosition> traversal = SnakeGridTraversal.create(
                                        positions.gridSize(),
                                        SnakeTraversalStrategy.LEFT_TO_RIGHT_SNAKE);

                        executeTraversal(
                                        traversal,
                                        positions.values(),
                                        request.keySpeed());

                        log.info(
                                        "Tango completed. executionId={}, gridSize={}",
                                        executionId,
                                        positions.gridSize());

                } catch (Exception e) {
                        throw new TangoCommandExecutionException(
                                        "Tango keyboard automation failed",
                                        executionId,
                                        e);
                }
        }

        private void pressValue(
                        GridPosition position,
                        int value) {
                char digit = Character.forDigit(value, 10);

                keyboard.typeLetter(digit);

                log.info(
                                "Entering Sudoku value {} at grid position {}",
                                digit,
                                position);
        }

        private void executeTraversal(
                        List<GridPosition> traversal,
                        Map<GridPosition, Integer> values,
                        int keySpeed) {
                for (int index = 0; index < traversal.size(); index++) {

                        GridPosition current = traversal.get(index);

                        int value = values.get(current);

                        pressValue(current, value);

                        if (index + 1 < traversal.size()) {
                                gridTraversal.move(
                                                current,
                                                traversal.get(index + 1));

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
                mouseService.click(LinkedInGame.MINI_SUDOKU.location().x(), LinkedInGame.MINI_SUDOKU.location().y());

                keyboard.addDelay(100);
                keyboard.pressTab();
                keyboard.addDelay(100);
                keyboard.pressLeft();
                keyboard.addDelay(100);
        }
}