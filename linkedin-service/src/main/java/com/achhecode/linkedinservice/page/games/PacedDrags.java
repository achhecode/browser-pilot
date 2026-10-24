package com.achhecode.linkedinservice.page.games;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import com.microsoft.playwright.Mouse;
import com.microsoft.playwright.Page;

/**
 * Collects press / move / release actions for one or more drags and replays
 * them evenly spread over a total duration between MIN_MS and MAX_MS.
 */
final class PacedDrags {

    /** Total input time window (ms). Margin inside 2-3 s covers network jitter. */
    private static final long MIN_MS = 2_300;
    private static final long MAX_MS = 2_700;

    /** Interpolation steps per move; adjacent cells need very few. */
    private static final int MOVE_STEPS = 2;

    private enum Kind {
        PRESS, MOVE, RELEASE
    }

    private record Action(Kind kind, double x, double y) {
    }

    private final List<Action> actions = new ArrayList<>();

    /** Reads every cell centre (viewport coordinates) in ONE browser round trip. */
    static Map<Integer, double[]> cellCentres(Page page, String cellSelector) {
        page.locator(cellSelector).first().scrollIntoViewIfNeeded();

        Object raw = page.locator(cellSelector).evaluateAll("els => els.map(e => {"
                + " const r = e.getBoundingClientRect();"
                + " return [Number(e.getAttribute('data-cell-idx')), r.x + r.width / 2, r.y + r.height / 2]; })");

        Map<Integer, double[]> centres = new HashMap<>();
        if (raw instanceof List<?> rows) {
            for (Object o : rows) {
                if (o instanceof List<?> row && row.size() == 3) {
                    int idx = ((Number) row.get(0)).intValue();
                    double x = ((Number) row.get(1)).doubleValue();
                    double y = ((Number) row.get(2)).doubleValue();
                    centres.put(idx, new double[] { x, y });
                }
            }
        }
        return centres;
    }

    /**
     * Queues one drag: press on the first point, move through the rest, release.
     */
    void drag(List<double[]> points) {
        if (points.size() < 2) {
            return;
        }
        double[] first = points.get(0);
        actions.add(new Action(Kind.PRESS, first[0], first[1]));
        for (int i = 1; i < points.size(); i++) {
            double[] p = points.get(i);
            actions.add(new Action(Kind.MOVE, p[0], p[1]));
        }
        double[] last = points.get(points.size() - 1);
        actions.add(new Action(Kind.RELEASE, last[0], last[1]));
    }

    /** Replays every queued action so the whole run lasts the chosen duration. */
    void run(Page page) {
        if (actions.isEmpty()) {
            return;
        }

        long budgetMs = ThreadLocalRandom.current().nextLong(MIN_MS, MAX_MS + 1);
        int n = actions.size();
        Mouse mouse = page.mouse();
        boolean held = false;

        long start = System.nanoTime();
        try {
            for (int i = 0; i < n; i++) {
                sleepUntil(start, budgetMs * i / n);

                Action a = actions.get(i);
                switch (a.kind()) {
                case PRESS -> {
                    mouse.move(a.x(), a.y());
                    mouse.down();
                    held = true;
                }
                case MOVE -> mouse.move(a.x(), a.y(), new Mouse.MoveOptions().setSteps(MOVE_STEPS));
                case RELEASE -> {
                    mouse.up();
                    held = false;
                }
                }
            }
            // Never finish earlier than the budget.
            sleepUntil(start, budgetMs);
        } finally {
            if (held) {
                mouse.up(); // never leave the button held after a failure
            }
        }
    }

    private static void sleepUntil(long startNanos, long targetMs) {
        long remainingMs = targetMs - (System.nanoTime() - startNanos) / 1_000_000;
        if (remainingMs > 0) {
            try {
                Thread.sleep(remainingMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Interrupted while pacing input", e);
            }
        }
    }
}