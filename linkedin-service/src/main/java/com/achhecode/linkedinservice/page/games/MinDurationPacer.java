package com.achhecode.linkedinservice.page.games;

import java.util.concurrent.TimeUnit;

import com.microsoft.playwright.Page;

/**
 * Spreads N steps evenly over a minimum duration using a monotonic clock, so
 * the final step is never executed before the deadline.
 * <p>
 * Each step has an absolute due time, so a slow step shortens the next wait
 * instead of adding to the total runtime.
 */
public final class MinDurationPacer {

    private final Page page;
    private final long totalNanos;
    private final int totalSteps;
    private final long startNanos;

    private MinDurationPacer(Page page, long minDurationMs, int totalSteps) {
        if (totalSteps <= 0) {
            throw new IllegalArgumentException("totalSteps must be > 0");
        }
        this.page = page;
        this.totalNanos = TimeUnit.MILLISECONDS.toNanos(minDurationMs);
        this.totalSteps = totalSteps;
        this.startNanos = System.nanoTime();
    }

    /** Starts the clock now. Call right before the first step. */
    public static MinDurationPacer start(Page page, long minDurationMs, int totalSteps) {
        return new MinDurationPacer(page, minDurationMs, totalSteps);
    }

    /**
     * Blocks until step {@code index} (0-based) is due. The last step is due
     * exactly at the full duration.
     */
    public void awaitStep(int index) {
        if (index < 0 || index >= totalSteps) {
            throw new IndexOutOfBoundsException("Step " + index + " of " + totalSteps);
        }
        long dueAt = startNanos + totalNanos * (index + 1) / totalSteps;
        long remaining = dueAt - System.nanoTime();
        if (remaining > 0) {
            page.waitForTimeout(remaining / 1_000_000.0);
        }
    }
}