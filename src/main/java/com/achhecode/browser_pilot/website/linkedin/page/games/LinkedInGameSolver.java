package com.achhecode.browser_pilot.website.linkedin.page.games;

public interface LinkedInGameSolver<R, P> {
    void solve(R request);

    void solve(P page, R request);
}