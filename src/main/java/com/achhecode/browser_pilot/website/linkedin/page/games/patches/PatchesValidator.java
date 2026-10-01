package com.achhecode.browser_pilot.website.linkedin.page.games.patches;

import com.achhecode.browser_pilot.grid.GridDirection;
import com.achhecode.browser_pilot.grid.GridPosition;
import com.achhecode.browser_pilot.grid.GridSize;
import org.springframework.stereotype.Component;

@Component
public class PatchesValidator {

    public void validate(PatchesRequest request) {

        GridSize gridSize =
                new GridSize(
                        request.gridHeight(),
                        request.gridWidth()
                );

        for (int index = 0;
             index < request.patches().size();
             index++) {

            PatchesRequest.Patch patch =
                    request.patches().get(index);

            GridPosition start =
                    toPosition(patch, index);

            if (!gridSize.contains(start)) {
                throw invalid(
                        index,
                        "Start position is outside grid: "
                                + start
                );
            }

            GridPosition end = start;

            for (GridDirection direction : patch.move()) {

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
            PatchesRequest.Patch patch,
            int index
    ) {
        return new GridPosition(
                patch.position().get(0),
                patch.position().get(1)
        );
    }

    private IllegalArgumentException invalid(
            int index,
            String message
    ) {
        return new IllegalArgumentException(
                "Invalid patch at index "
                        + index
                        + ": "
                        + message
        );
    }
}