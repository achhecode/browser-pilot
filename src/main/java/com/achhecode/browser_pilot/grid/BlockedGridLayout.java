package com.achhecode.browser_pilot.grid;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public final class BlockedGridLayout {

    private final int rows;
    private final int cols;
    private final boolean[][] blocked;

    public BlockedGridLayout(int rows, int cols, Collection<GridPosition> blockedCells) {
        if (rows <= 0 || cols <= 0) {
            throw new IllegalArgumentException("Grid size must be positive");
        }
        this.rows = rows;
        this.cols = cols;
        this.blocked = new boolean[rows][cols];
        for (GridPosition p : blockedCells) {
            requireInside(p.row(), p.column());
            blocked[p.row()][p.column()] = true;
        }
    }

    public int rows() { return rows; }
    public int cols() { return cols; }

    public boolean isBlocked(int row, int col) { return blocked[row][col]; }

    public void requireOpen(GridPosition p) {
        requireInside(p.row(), p.column());
        if (blocked[p.row()][p.column()]) {
            throw new IllegalArgumentException("Position is blocked: " + p);
        }
    }

    /** First open cell in row-major order: where Tab puts focus. For your grid this is (0,4). */
    public GridPosition firstOpenCell() {
        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++)
                if (!blocked[r][c]) return new GridPosition(r, c);
        throw new IllegalStateException("Grid has no open cells");
    }

    public Set<GridPosition> openCells() {
        Set<GridPosition> result = new HashSet<>();
        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++)
                if (!blocked[r][c]) result.add(new GridPosition(r, c));
        return result;
    }

    private void requireInside(int row, int col) {
        if (row < 0 || row >= rows || col < 0 || col >= cols) {
            throw new IllegalArgumentException(
                    "Position out of bounds: (" + row + "," + col + ")");
        }
    }

    /** Next open cell in a direction: wraps around edges and skips blocked cells. */
    public GridPosition next(GridPosition p, GridDirection direction) {
        int dr = 0, dc = 0;
        switch (direction) {
            case UP -> dr = -1;
            case DOWN -> dr = 1;
            case LEFT -> dc = -1;
            case RIGHT -> dc = 1;
        }
        int r = p.row(), c = p.column();
        for (int i = 0; i < Math.max(rows, cols); i++) {
            r = Math.floorMod(r + dr, rows);
            c = Math.floorMod(c + dc, cols);
            if (!blocked[r][c]) {
                return new GridPosition(r, c);
            }
        }
        return p;
    }
}