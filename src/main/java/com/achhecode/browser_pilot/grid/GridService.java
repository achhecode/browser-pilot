package com.achhecode.browser_pilot.grid;

import org.springframework.stereotype.Service;

@Service
public class GridService {

    public String generateGrid(GridRequest request) {
        return GridPrinter.empty(
                request.rows(),
                request.columns()
        );
    }
}