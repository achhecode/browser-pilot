package com.achhecode.browser_pilot.website.linkedin.page.games.crosslimb;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.achhecode.browser_pilot.keyboard.KeyboardService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CrossLimbCommandExecutor {

    private final KeyboardService keyboard;

    public void execute(List<String> words, String executionId) {
        log.info("Entering the game");
        enterGame();
        log.info("[{}] Cross Limb started with {} words", executionId, words.size());

        for (String word : words) {
            log.info("[{}] Typing word: {}", executionId, word);
            typeWord(word);
            // keyboard.pressEnter();   // submit the word (remove if the game auto-advances)
            keyboard.addDelay(200);  // let the game register the word before the next one
        }

        log.info("[{}] Cross Limb finished", executionId);
    }

    private void typeWord(String word) {
        for (char letter : word.toCharArray()) {
            keyboard.typeLetter(letter);
        }
    }

    private void enterGame(){
        keyboard.switchTab();
        keyboard.pressEnter();
    }
}