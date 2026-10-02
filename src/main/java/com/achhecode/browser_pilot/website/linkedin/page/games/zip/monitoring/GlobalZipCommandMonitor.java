package com.achhecode.browser_pilot.website.linkedin.page.games.zip.monitoring;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class GlobalZipCommandMonitor implements ZipCommandMonitor, NativeKeyListener {

    private final List<String> recordedCommands = new ArrayList<>();

    private volatile boolean running = false;
    private volatile boolean hookRegistered = false;

    private synchronized boolean registerNativeHook() {

        if (hookRegistered) {
            return true;
        }

        try {

            GlobalScreen.registerNativeHook();
            GlobalScreen.addNativeKeyListener(this);

            hookRegistered = true;

            log.info(
                    "JNativeHook global keyboard hook registered successfully"
            );

            return true;

        } catch (NativeHookException e) {

            hookRegistered = false;

            log.error(
                    "Unable to register JNativeHook global keyboard hook.",
                    e
            );

            return false;

        } catch (Throwable e) {

            hookRegistered = false;

            log.error(
                    "Unexpected error while initializing JNativeHook.",
                    e
            );

            return false;
        }
    }

    @Override
    public synchronized void start() {

        if (!hookRegistered && !registerNativeHook()) {

            throw new IllegalStateException(
                    "Global keyboard monitoring is unavailable. " +
                    "JNativeHook could not be initialized."
            );
        }

        if (running) {
            log.warn("Keyboard monitoring is already running");
            return;
        }

        recordedCommands.clear();
        running = true;

        log.info("Global keyboard monitoring started");
    }

    @Override
    public synchronized List<String> stop() {

        if (!running) {
            log.warn("Keyboard monitoring is not running");
            return List.of();
        }

        running = false;

        List<String> result = List.copyOf(recordedCommands);

        log.info(
            "Global keyboard monitoring stopped. commandCount={}",
            result.size()
        );

        return result;
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public synchronized List<String> getRecordedCommands() {
        return List.copyOf(recordedCommands);
    }

    @Override
    public synchronized void nativeKeyPressed(NativeKeyEvent event) {

        if (!running) {
            return;
        }

        String command = switch (event.getKeyCode()) {

            case NativeKeyEvent.VC_UP -> "UP";

            case NativeKeyEvent.VC_DOWN -> "DOWN";

            case NativeKeyEvent.VC_LEFT -> "LEFT";

            case NativeKeyEvent.VC_RIGHT -> "RIGHT";

            default -> null;
        };

        if (command == null) {
            return;
        }

        recordedCommands.add(command);

        log.debug(
            "Arrow key captured: {}. total={}",
            command,
            recordedCommands.size()
        );
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent event) {
        // We only care about key presses.
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent event) {
        // Not required.
    }

    @PreDestroy
    public synchronized void shutdown() {

        running = false;

        if (!hookRegistered) {
            return;
        }

        try {

            GlobalScreen.removeNativeKeyListener(this);
            GlobalScreen.unregisterNativeHook();

            hookRegistered = false;

            log.info("JNativeHook global keyboard hook unregistered");

        } catch (NativeHookException e) {

            log.warn(
                "Failed to unregister JNativeHook during shutdown",
                e
            );
        }
    }
}