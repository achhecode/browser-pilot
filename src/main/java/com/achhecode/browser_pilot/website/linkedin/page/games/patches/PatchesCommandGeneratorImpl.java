package com.achhecode.browser_pilot.website.linkedin.page.games.patches;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.achhecode.browser_pilot.website.linkedin.page.games.patches.PatchesCommand.*;

@Component
public class PatchesCommandGeneratorImpl implements PatchesCommandGenerator {

    /** 0 = no wrap-around. Set both if the cursor wraps at the board edges. */
    @Value("${automation.patches.grid-width:0}")
    private int gridWidth;

    @Value("${automation.patches.grid-height:0}")
    private int gridHeight;

    @Override
public List<PatchesCommand> generate(List<PatchesCommandInput.Patch> patches) {
    List<PatchesCommand> out = new ArrayList<>();
    int x = 0, y = 0;

    for (PatchesCommandInput.Patch patch : patches) {
        int targetX = patch.position().get(0);
        int targetY = patch.position().get(1);

            // Travel (drawing off) from the cursor to the patch start
            travel(out, x, targetX, gridWidth, LEFT, RIGHT);
            travel(out, y, targetY, gridHeight, DOWN, UP);
            x = targetX;
            y = targetY;

            // Space, draw the moves, space
            out.add(SPACE);
            for (PatchesCommand move : patch.move()) {
                out.add(move);
                x += dx(move);
                y += dy(move);
            }
            out.add(SPACE);
        }
        return out;
    }

    /** Adds the fewest presses to go from -> to on one axis. */
    private void travel(List<PatchesCommand> out, int from, int to, int size,
                        PatchesCommand plus, PatchesCommand minus) {
        int diff = to - from;
        if (size > 0) { // wrap-around: pick the shorter direction
            diff = Math.floorMod(diff, size);
            if (diff > size - diff) diff -= size;
        }
        PatchesCommand key = diff >= 0 ? plus : minus;
        for (int i = 0; i < Math.abs(diff); i++) out.add(key);
    }

    private int dx(PatchesCommand c) { return c == LEFT ? 1 : c == RIGHT ? -1 : 0; }
    private int dy(PatchesCommand c) { return c == DOWN ? 1 : c == UP ? -1 : 0; }
}