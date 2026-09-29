package com.achhecode.browser_pilot.website.linkedin.page.games.patches;

import java.util.List;

public record PatchesRequest(Integer gridWidth, Integer gridHeight, List<Patch> patches) {

    public int width()  { return gridWidth  == null ? 6 : gridWidth; }
    public int height() { return gridHeight == null ? 6 : gridHeight; }

    public record Patch(List<Integer> position, List<PatchesCommand> move) {}
}

/*        
        
{
  "patches": [
    {
      "position": [0, 0],
      "move": ["LEFT", "LEFT"]
    },
    {
      "position": [2, 0],
      "move": ["LEFT", "LEFT", "DOWN", "DOWN", "DOWN"]
    },
    {
      "position": [5, 0],
      "move": ["DOWN", "DOWN", "DOWN", "DOWN", "DOWN"]
    }
  ]
}

*/