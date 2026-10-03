package com.achhecode.browser_pilot.website.linkedin.page.games.pinpoint;

import com.achhecode.browser_pilot.keyboard.KeyboardService;
import com.achhecode.browser_pilot.website.linkedin.page.games.zip.ZipCommandExecutionException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class PinpointCommandExecutorImpl
        implements PinpointCommandExecutor {

    private final KeyboardService keyboard;

    public PinpointCommandExecutorImpl(
            KeyboardService keyboard
    ) {
        this.keyboard = keyboard;
    }

    @Override
    public synchronized void execute(
            PinpointRequest request,
            String executionId
    ) {
        long startTime = System.nanoTime();

        try {

            prepare(request.onlyKey());

            enterEachWord(
                    request.answer(),
                    request.keySpeed()
            );

            long totalMs =
                    (System.nanoTime() - startTime) / 1_000_000;

            log.info(
                    "Pinpoint completed. executionId={}, " +
                    "keySpeed={}, totalMs={}",
                    executionId,
                    request.keySpeed(),
                    totalMs
            );

        } catch (Exception e) {

            log.error(
                    "Zip execution failed. executionId={}",
                    executionId,
                    e
            );

            throw new ZipCommandExecutionException(
                    "Zip keyboard automation failed",
                    executionId,
                    e
            );
        }
    }

    private void enterEachWord(String answer, int keySpeed){
        for(char c: answer.toCharArray()){
                keyboard.typeLetter(c);
                keyboard.addDelay(keySpeed);
        }
        keyboard.pressEnter();
    }

    private void prepare(boolean onlyKey) {

        if(onlyKey) keyboard.switchTab();
    }
}