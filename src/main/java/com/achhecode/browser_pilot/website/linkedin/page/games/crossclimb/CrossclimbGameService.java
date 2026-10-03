package com.achhecode.browser_pilot.website.linkedin.page.games.crossclimb;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.achhecode.browser_pilot.keyboard.KeyboardService;

import lombok.extern.slf4j.Slf4j;

@Slf4j 
@Service
public class CrossclimbGameService {

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

        Set<String> solvedClues = new HashSet<>();

        while (solvedClues.size() < request.clues().size()) {

            String currentClue = page.getCurrentClue();
            String normalizedClue = normalize(currentClue);

            if (solvedClues.contains(normalizedClue)) {
                throw new IllegalStateException(
                        "Crossclimb returned an already solved clue: '%s'"
                                .formatted(currentClue)
                );
            }

            // CrossclimbClue clue = request.clues()
            //         .stream()
            //         .filter(candidate ->
            //                 normalize(candidate.clue())
            //                         .equals(normalizedClue)
            //         )
            //         .findFirst()
            //         .orElseThrow(() ->
            //                 new IllegalStateException(
            //                         "No answer found for Crossclimb clue: '%s'"
            //                                 .formatted(currentClue)
            //                 )
            //         );

            Optional<CrossclimbClue> clue = request.clues()
                    .stream()
                    .filter(candidate ->
                            normalize(candidate.clue())
                                    .equals(normalizedClue)
                    )
                    .findFirst();

            if (clue.isEmpty()) {
                continue;
            }

            CrossclimbClue matchedClue = clue.get();

            keyboardService.typeAnswer(matchedClue.answer());

            solvedClues.add(normalizedClue);

            // if (solvedClues.size() < request.clues().size()
            //         && !page.waitForClueToChange(currentClue)) {

            //     throw new IllegalStateException(
            //             "Crossclimb clue did not change after entering answer: "
            //                     + clue.get().answer()
            //     );
            // }

            if (solvedClues.size() < request.clues().size()
                    && !page.waitForClueToChange(currentClue)) {
                continue;
            }
        }

        // All clues have been solved.
        arrangeBoard(page, request);
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

    private String normalize(String clue) {
        return clue
                .trim()
                .replaceAll("\\s+", " ")
                .toLowerCase();
    }


    private void arrangeBoard(
        CrossclimbPage page,
        CrossclimbRequest request
        ) {
            List<String> words = page.getBoardWords();

            List<String> desiredOrder = arrangeWords(words);

            page.arrangeWords(desiredOrder);
        }

        private List<String> arrangeWords(List<String> words) {
        for (String start : words) {
            List<String> path = findPath(
                    words,
                    start,
                    new ArrayList<>()
            );

            if (path != null) {
                return path;
            }
        }

        throw new IllegalStateException(
                "Could not arrange Crossclimb words into a valid chain"
        );
    }

    private List<String> findPath(
            List<String> words,
            String current,
            List<String> path
    ) {
        path.add(current);

        if (path.size() == words.size()) {
            return new ArrayList<>(path);
        }

        for (String next : words) {
            if (!path.contains(next)
                    && differsByOneLetter(current, next)) {

                List<String> result = findPath(
                        words,
                        next,
                        path
                );

                if (result != null) {
                    return result;
                }
            }
        }

        path.remove(path.size() - 1);

        return null;
    }

    private boolean differsByOneLetter(
            String first,
            String second
    ) {
        if (first.length() != second.length()) {
            return false;
        }

        int differences = 0;

        for (int i = 0; i < first.length(); i++) {
            if (first.charAt(i) != second.charAt(i)) {
                differences++;

                if (differences > 1) {
                    return false;
                }
            }
        }

        return differences == 1;
    }
}