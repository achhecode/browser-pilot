package com.achhecode.browser_pilot.website.linkedin.page.games.wend;

import com.achhecode.browser_pilot.grid.GridDirection;
import com.achhecode.browser_pilot.grid.GridPosition;
import org.springframework.stereotype.Component;

@Component
public class WendMapper {

    public WendPath map(
            WendRequest.Wend wend
    ) {
        GridPosition start =
                new GridPosition(
                        wend.position().get(0),
                        wend.position().get(1)
                );

        GridPosition end = start;

        for (GridDirection direction : wend.move()) {
            end = direction.move(end);
        }

        return new WendPath(
                start,
                wend.move(),
                end
        );
    }
}