package com.achhecode.browser_pilot.website.linkedin.page.games.crossclimb;

import java.util.List;

import org.springframework.stereotype.Service;

import com.achhecode.browser_pilot.keyboard.KeyboardService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CrossclimbGameService {

    private static final String FINAL_CLUE = "The top + bottom rows =";

    private final KeyboardService keyboardService;

    public CrossclimbGameService(KeyboardService keyboardService) {
        this.keyboardService = keyboardService;
    }

    public void solve(
            CrossclimbPage page,
            CrossclimbRequest request
    ) {
        page.waitUntilReady();

        String currentClue = page.getCurrentClue();

        if (currentClue.contains(FINAL_CLUE)) {
            log.info("Already at final clue: {}", currentClue);
            typeStartAndEnd(request.start(), request.end());
            return;
        }

        solveClues(page, request.clues());

        if (!page.waitUntilStartAndEndClueAppears(FINAL_CLUE)) {
            throw new IllegalStateException(
                    "Crossclimb start/end clue did not appear"
            );
        }

        typeStartAndEnd(request.start(), request.end());
    }

    private void solveClues(
            CrossclimbPage page,
            List<CrossclimbClue> clues
    ) {
        for (int i = 0; i < clues.size(); i++) {
            CrossclimbClue clue = clues.get(i);
            String currentClue = page.getCurrentClue();

            keyboardService.typeAnswer(clue.answer());

            if (i < clues.size() - 1
                    && !page.waitForClueToChange(currentClue)) {
                throw new IllegalStateException(
                        "Crossclimb clue did not change after answering: "
                                + currentClue
                );
            }
        }
    }

    public void solve(CrossclimbRequest request) {
        for (CrossclimbClue clue : request.clues()) {
            keyboardService.typeAnswer(clue.answer());
            keyboardService.addDelay(1000);
        }
    }

    private void typeStartAndEnd(
            String start,
            String end
    ) {
        keyboardService.typeAnswer(start);
        keyboardService.addDelay(1000);
        keyboardService.typeAnswer(end);
    }
}