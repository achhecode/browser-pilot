package com.achhecode.browser_pilot.grid;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import com.achhecode.browser_pilot.keyboard.KeyboardService;

public class WrappingGridNavigator {

    private final KeyboardService keyboard;
    private final BlockedGridLayout layout;

    public WrappingGridNavigator(KeyboardService keyboard, BlockedGridLayout layout) {
        this.keyboard = keyboard;
        this.layout = layout;
    }

    public GridPosition start() {
        return layout.firstOpenCell();
    }

    /** One key press: wraps around edges and skips blocked cells. */
    public GridPosition move(GridPosition from, GridDirection direction) {
        layout.requireOpen(from);
        GridPosition next = next(from, direction);
        pressKey(direction);
        return next;
    }

    /** Shortest key sequence from -> to, using wrap-around and skipping blockers. */
    public GridPosition moveTo(GridPosition from, GridPosition to) {
        layout.requireOpen(from);
        layout.requireOpen(to);

        for (GridDirection d : shortestPath(from, to)) {
            pressKey(d);
        }
        return to;
    }

    // ---------- pure logic (no keyboard) ----------

    // GridPosition next(GridPosition p, GridDirection direction) {
    //     int dr = 0, dc = 0;
    //     switch (direction) {
    //         case UP -> dr = -1;
    //         case DOWN -> dr = 1;
    //         case LEFT -> dc = -1;
    //         case RIGHT -> dc = 1;
    //     }
    //     int rows = layout.rows(), cols = layout.cols();
    //     int r = p.row(), c = p.column();

    //     // At most max(rows, cols) steps: the origin cell itself is open,
    //     // so the loop always terminates (worst case it returns to itself).
    //     for (int i = 0; i < Math.max(rows, cols); i++) {
    //         r = Math.floorMod(r + dr, rows);
    //         c = Math.floorMod(c + dc, cols);
    //         if (!layout.isBlocked(r, c)) {
    //             return new GridPosition(r, c);
    //         }
    //     }
    //     return p;
    // }

    GridPosition next(GridPosition p, GridDirection direction) {
        return layout.next(p, direction);
    }

    List<GridDirection> shortestPath(GridPosition from, GridPosition to) {
        if (from.equals(to)) return List.of();

        Map<GridPosition, GridPosition> parent = new HashMap<>();
        Map<GridPosition, GridDirection> via = new HashMap<>();
        Queue<GridPosition> queue = new ArrayDeque<>();
        parent.put(from, from);
        queue.add(from);

        while (!queue.isEmpty()) {
            GridPosition cur = queue.poll();
            if (cur.equals(to)) break;
            for (GridDirection d : GridDirection.values()) {
                GridPosition n = next(cur, d);
                if (parent.putIfAbsent(n, cur) == null) {
                    via.put(n, d);
                    queue.add(n);
                }
            }
        }

        List<GridDirection> path = new ArrayList<>();
        for (GridPosition cur = to; !cur.equals(from); cur = parent.get(cur)) {
            path.add(via.get(cur));
        }
        Collections.reverse(path);
        return path;
    }

    private void pressKey(GridDirection d) {
        switch (d) {
            case UP -> keyboard.pressUp();
            case DOWN -> keyboard.pressDown();
            case LEFT -> keyboard.pressLeft();
            case RIGHT -> keyboard.pressRight();
        }
    }
}