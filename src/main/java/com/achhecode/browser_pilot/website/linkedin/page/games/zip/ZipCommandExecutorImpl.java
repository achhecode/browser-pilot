package com.achhecode.browser_pilot.website.linkedin.page.games.zip;

import com.achhecode.browser_pilot.keyboard.ArrowDirection;
import com.achhecode.browser_pilot.keyboard.KeyboardService;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class ZipCommandExecutorImpl implements ZipCommandExecutor {

    public interface CoreGraphics extends Library {
        CoreGraphics INSTANCE = Native.load("CoreGraphics", CoreGraphics.class);

        Pointer CGEventSourceCreate(int stateID);
        Pointer CGEventCreateKeyboardEvent(Pointer source, short virtualKey, boolean keyDown);
        void CGEventPost(int tap, Pointer event);
        void CFRelease(Pointer obj);
    }

    /**
     * Delay in milliseconds after each arrow command, so the target
     * application has time to actually process the keystroke before the
     * next one arrives. Override via application.properties/yml or an
     * env var, e.g.:
     *   automation.keyboard.command-delay-ms=20
     */
    @Value("${automation.keyboard.command-delay-ms:20}")
    private long commandDelayMs;

    /**
     * Delay in milliseconds after the Cmd+Tab application switch, before
     * the first arrow command is sent — gives the target app time to
     * actually gain focus. Override via:
     *   automation.keyboard.post-switch-delay-ms=200
     */
    @Value("${automation.keyboard.post-switch-delay-ms:200}")
    private long postSwitchDelayMs;

    @Value("${automation.keyboard.combo-delay-ms:50}")
    private long comboDelayMs;

    private final KeyboardService keyboard;

    public ZipCommandExecutorImpl(KeyboardService keyboard) {
        this.keyboard = keyboard;
    }

    @Override
    public synchronized void execute(List<ArrowDirection> commands, String executionId) {
        if (commands == null || commands.isEmpty()) {
            log.warn("No keyboard commands to execute. executionId={}", executionId);
            return;
        }

        long totalStart = System.nanoTime();

        try {

            for (int i = 0; i < commands.size(); i++) {
                int macKey = commands.get(i).getKeyCode();
                keyboard.press(macKey);

                boolean isLastCommand = i == commands.size() - 1;
                if (commandDelayMs > 0 && !isLastCommand) {
                    keyboard.addDelay(commandDelayMs);
                }
            }

            long totalMs = (System.nanoTime() - totalStart) / 1_000_000;
            log.info(
                "Fast keyboard automation completed. executionId={}, commandCount={}, totalMs={}",
                executionId, commands.size(), totalMs
            );

        } catch (Exception e) {
            log.error("Fast keyboard automation failed. executionId={}", executionId, e);
            throw new ZipCommandExecutionException("Keyboard automation failed", executionId, e);
        }
    }
    
}