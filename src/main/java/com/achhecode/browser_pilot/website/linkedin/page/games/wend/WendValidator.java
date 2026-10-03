package com.achhecode.browser_pilot.website.linkedin.page.games.wend;

import com.achhecode.browser_pilot.grid.GridDirection;
import com.achhecode.browser_pilot.grid.GridPosition;
import com.achhecode.browser_pilot.grid.GridSize;
import org.springframework.stereotype.Component;

@Component
public class WendValidator {

    public void validate(WendRequest request) {

        GridSize gridSize =
                new GridSize(
                        request.gridHeight(),
                        request.gridWidth()
                );

        for (int index = 0;
             index < request.wend().size();
             index++) {

            WendRequest.Wend wend =
                    request.wend().get(index);

            GridPosition start =
                    toPosition(wend, index);

            if (!gridSize.contains(start)) {
                throw invalid(
                        index,
                        "Start position is outside grid: "
                                + start
                );
            }

            GridPosition end = start;

            for (GridDirection direction : wend.move()) {

                end = direction.move(end);

                if (!gridSize.contains(end)) {
                    throw invalid(
                            index,
                            "Move " + direction
                                    + " leaves grid at "
                                    + end
                    );
                }
            }
        }
    }

    private GridPosition toPosition(
            WendRequest.Wend wend,
            int index
    ) {
        return new GridPosition(
                wend.position().get(0),
                wend.position().get(1)
        );
    }

    private IllegalArgumentException invalid(
            int index,
            String message
    ) {
        return new IllegalArgumentException(
                "Invalid wend at index "
                        + index
                        + ": "
                        + message
        );
    }
}