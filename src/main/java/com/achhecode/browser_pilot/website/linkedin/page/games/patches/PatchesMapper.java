package com.achhecode.browser_pilot.website.linkedin.page.games.patches;

import com.achhecode.browser_pilot.grid.GridDirection;
import com.achhecode.browser_pilot.grid.GridPosition;
import org.springframework.stereotype.Component;

@Component
public class PatchesMapper {

    public PatchPath map(
            PatchesRequest.Patch patch
    ) {
        GridPosition start =
                new GridPosition(
                        patch.position().get(0),
                        patch.position().get(1)
                );

        GridPosition end = start;

        for (GridDirection direction : patch.move()) {
            end = direction.move(end);
        }

        return new PatchPath(
                start,
                patch.move(),
                end
        );
    }
}