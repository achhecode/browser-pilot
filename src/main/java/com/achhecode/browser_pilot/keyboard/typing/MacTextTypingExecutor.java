package com.achhecode.browser_pilot.keyboard.typing;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;

@Slf4j
@Component
public class MacTextTypingExecutor implements TextTypingExecutor {

    private Robot robot;

    @Value("${automation.keyboard.switch-application:true}")
    private boolean switchApplication;

    private Robot getRobot() {
        if (robot == null) {
            try {
                robot = new Robot();

                log.info("Java Robot initialized for text typing");

            } catch (AWTException e) {
                log.error(
                        "Unable to initialize Java Robot",
                        e
                );

                throw new IllegalStateException(
                        "Unable to initialize Java Robot",
                        e
                );
            }
        }

        return robot;
    }

    @Override
    public void type(
            String text,
            String executionId
    ) {

        if (text == null || text.isEmpty()) {
            log.warn(
                    "Empty text received. executionId={}",
                    executionId
            );
            return;
        }

        Robot robot = getRobot();

        try {

            /*
             * -------------------------------------------------
             * 1. Switch to previous application
             * -------------------------------------------------
             *
             * Example:
             *
             * Postman -> Brave
             *
             * Command + Tab switches to the previous application.
             */
            if (switchApplication) {

                log.debug(
                        "Switching to previous application. executionId={}",
                        executionId
                );

                robot.keyPress(KeyEvent.VK_META);

                try {
                    robot.keyPress(KeyEvent.VK_TAB);
                    robot.keyRelease(KeyEvent.VK_TAB);
                } finally {
                    robot.keyRelease(KeyEvent.VK_META);
                }
            }

            /*
             * -------------------------------------------------
             * 2. Allow target application to receive focus
             * -------------------------------------------------
             */

            long start = System.nanoTime();

            /*
             * -------------------------------------------------
             * 3. Put complete text into macOS clipboard
             * -------------------------------------------------
             */
            Clipboard clipboard =
                    Toolkit.getDefaultToolkit()
                            .getSystemClipboard();

            StringSelection selection =
                    new StringSelection(text);

            clipboard.setContents(
                    selection,
                    null
            );

            /*
             * -------------------------------------------------
             * 4. Paste using Command + V
             * -------------------------------------------------
             */
            robot.keyPress(KeyEvent.VK_META);

            try {
                robot.keyPress(KeyEvent.VK_V);
                robot.keyRelease(KeyEvent.VK_V);
            } finally {
                robot.keyRelease(KeyEvent.VK_META);
            }

            long elapsedMs =
                    (System.nanoTime() - start)
                            / 1_000_000;

            log.info(
                    "Fast text typing completed. " +
                    "executionId={}, characters={}, elapsedMs={}",
                    executionId,
                    text.length(),
                    elapsedMs
            );

        } catch (Exception e) {

            log.error(
                    "Fast text typing failed. executionId={}",
                    executionId,
                    e
            );

            throw new KeyboardExecutionException(
                    "Text typing failed",
                    executionId,
                    e
            );
        }
    }
}