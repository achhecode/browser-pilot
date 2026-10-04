package com.achhecode.browser_pilot.website.linkedin.page.games.wend;

import java.util.List;

import org.springframework.stereotype.Component;

@Component 
public class WendRequestMapper {

    public WendRequest map(WendRequest request) {
        return new WendRequest(
                request.gridWidth(),
                request.gridHeight(),
                request.routeStrategy(),
                request.blocked().stream()
                        .map(this::toInternalPosition)
                        .toList(),
                request.wend().stream()
                        .map(w -> new WendRequest.Wend(
                                toInternalPosition(w.position()),
                                w.move()
                        ))
                        .toList(),
                request.onlyKey(),
                request.keySpeed()
        );
    }

    private List<Integer> toInternalPosition(List<Integer> position) {
        int x = position.get(0); // 1-based column
        int y = position.get(1); // 1-based row

        // Existing internal format: [row, column], 0-based
        return List.of(
                y - 1,
                x - 1
        );
    }
}