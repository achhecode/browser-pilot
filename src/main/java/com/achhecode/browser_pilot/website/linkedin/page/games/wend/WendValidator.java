package com.achhecode.browser_pilot.website.linkedin.page.games.wend;

import com.achhecode.browser_pilot.grid.BlockedGridLayout;
import com.achhecode.browser_pilot.grid.GridDirection;
import com.achhecode.browser_pilot.grid.GridPosition;
import org.springframework.stereotype.Component;

@Component
public class WendValidator {

    public void validate(WendRequest request, BlockedGridLayout layout) {

        for (int index = 0; index < request.wend().size(); index++) {

            WendRequest.Wend wend = request.wend().get(index);
            GridPosition start = toPosition(wend);

            try {
                // throws if out of bounds or on a blocked cell
                layout.requireOpen(start);
            } catch (IllegalArgumentException e) {
                throw invalid(index, "Invalid start position " + start + ": " + e.getMessage());
            }

            // Moves never leave the grid (they wrap and skip blocked cells),
            // so only check for malformed input.
            for (GridDirection direction : wend.move()) {
                if (direction == null) {
                    throw invalid(index, "Move list contains a null direction");
                }
            }
        }
    }

    private GridPosition toPosition(WendRequest.Wend wend) {
        return new GridPosition(
                wend.position().get(0),
                wend.position().get(1));
    }

    private IllegalArgumentException invalid(int index, String message) {
        return new IllegalArgumentException(
                "Invalid wend at index " + index + ": " + message);
    }
}