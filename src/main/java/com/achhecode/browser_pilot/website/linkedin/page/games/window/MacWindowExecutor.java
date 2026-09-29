package com.achhecode.browser_pilot.website.linkedin.page.games.window;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;

@Slf4j
@Component
public class MacWindowExecutor implements WindowExecutor {

    private Robot robot;

    private Robot getRobot() {

        if (robot == null) {
            try {
                robot = new Robot();

                log.info("Mac window automation Robot initialized");

            } catch (AWTException e) {
                log.error("Unable to initialize Robot for window automation", e);

                throw new IllegalStateException(
                        "Unable to initialize macOS window automation",
                        e
                );
            }
        }

        return robot;
    }

    @Override
    public void execute(WindowCommand command) {

        Robot robot = getRobot();

        switch (command) {

            case NEXT_APPLICATION -> {

                log.debug("Switching to next macOS application");

                // Press Command
                robot.keyPress(KeyEvent.VK_META);

                try {
                    // Press Tab while Command is held
                    robot.keyPress(KeyEvent.VK_TAB);
                    robot.keyRelease(KeyEvent.VK_TAB);
                } finally {
                    // Always release Command
                    robot.keyRelease(KeyEvent.VK_META);
                }
            }
        }
    }
}