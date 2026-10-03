package com.achhecode.browser_pilot.website.linkedin.page.games.crossclimb;

// import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.achhecode.browser_pilot.keyboard.KeyboardService;

import lombok.extern.slf4j.Slf4j;

@Slf4j 
@Service
public class CrossclimbGameService {

    private static final String FINAL_CLUE = "The top + bottom rows =";

    private final KeyboardService keyboardService;

    public CrossclimbGameService(
            KeyboardService keyboardService
    ) {
        this.keyboardService = keyboardService;
    }


    public void solve(
        CrossclimbPage page,
        CrossclimbRequest request
    ) {
        page.waitUntilReady();

        CrossclimbStage currentStage = CrossclimbStage.START;

        if(page.getCurrentClue().contains(FINAL_CLUE)){
            log.info("Current clue is {}", page.getCurrentClue());
            currentStage = CrossclimbStage.FINAL_CLUE;
        }

        List<CrossclimbClue> clues = request.clues();

        if(currentStage.equals(CrossclimbStage.FINAL_CLUE)){
                typeStartAndEnd(request.start(), request.end());
                log.info("Already at final clue!!");
                return ;
        }else{
                for (int i = 0; i < clues.size(); i++) {

                CrossclimbClue clue = clues.get(i);

                String currentClue = page.getCurrentClue();

                keyboardService.typeAnswer(clue.answer());

                if (i < clues.size() - 1
                        && !page.waitForClueToChange(currentClue)) {
                    continue;
                }
            }

            if (!page.waitUntilStartAndEndClueAppears("The top + bottom rows =")) {
                throw new IllegalStateException(
                        "Crossclimb start/end clue did not appear"
                );
            }

            typeStartAndEnd(request.start(), request.end());
        }
    }

    public void solve(
        CrossclimbRequest request
    ) {
        for (CrossclimbClue clue : request.clues()) {

            keyboardService.typeAnswer(
                    clue.answer()
            );

            keyboardService.addDelay(1000);
        }
    }

    public void typeStartAndEnd(
        String start, String end
    ) {
        keyboardService.typeAnswer(start);
        keyboardService.addDelay(1000);
        keyboardService.typeAnswer(end);
    }


    // private void arrangeBoard(
    //     CrossclimbPage page,
    //     CrossclimbRequest request
    //     ) {
    //         List<String> words = page.getBoardWords();

    //         List<String> desiredOrder = arrangeWords(words);

    //         page.arrangeWords(desiredOrder);
    //     }

    //     private List<String> arrangeWords(List<String> words) {
    //     for (String start : words) {
    //         List<String> path = findPath(
    //                 words,
    //                 start,
    //                 new ArrayList<>()
    //         );

    //         if (path != null) {
    //             return path;
    //         }
    //     }

    //     throw new IllegalStateException(
    //             "Could not arrange Crossclimb words into a valid chain"
    //     );
    // }

    // private List<String> findPath(
    //         List<String> words,
    //         String current,
    //         List<String> path
    // ) {
    //     path.add(current);

    //     if (path.size() == words.size()) {
    //         return new ArrayList<>(path);
    //     }

    //     for (String next : words) {
    //         if (!path.contains(next)
    //                 && differsByOneLetter(current, next)) {

    //             List<String> result = findPath(
    //                     words,
    //                     next,
    //                     path
    //             );

    //             if (result != null) {
    //                 return result;
    //             }
    //         }
    //     }

    //     path.remove(path.size() - 1);

    //     return null;
    // }

    // private boolean differsByOneLetter(
    //         String first,
    //         String second
    // ) {
    //     if (first.length() != second.length()) {
    //         return false;
    //     }

    //     int differences = 0;

    //     for (int i = 0; i < first.length(); i++) {
    //         if (first.charAt(i) != second.charAt(i)) {
    //             differences++;

    //             if (differences > 1) {
    //                 return false;
    //             }
    //         }
    //     }

    //     return differences == 1;
    // }
}