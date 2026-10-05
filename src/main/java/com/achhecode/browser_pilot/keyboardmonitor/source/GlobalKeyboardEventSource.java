package com.achhecode.browser_pilot.keyboardmonitor.source;

import com.achhecode.browser_pilot.keyboardmonitor.domain.KeyboardEvent;
import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

@Component
public class GlobalKeyboardEventSource
        implements KeyboardEventSource, NativeKeyListener {

    private final AtomicBoolean running =
            new AtomicBoolean(false);

    private volatile Consumer<KeyboardEvent> eventConsumer;

    @Override
    public synchronized void start(
            Consumer<KeyboardEvent> eventConsumer
    ) {

        Objects.requireNonNull(
                eventConsumer,
                "eventConsumer must not be null"
        );

        if (running.get()) {
            return;
        }

        this.eventConsumer = eventConsumer;

        try {

            if (!GlobalScreen.isNativeHookRegistered()) {
                GlobalScreen.registerNativeHook();
            }

            GlobalScreen.addNativeKeyListener(this);

            running.set(true);

        } catch (NativeHookException e) {

            this.eventConsumer = null;

            throw new IllegalStateException(
                    "Unable to start global keyboard hook",
                    e
            );
        }
    }

    @Override
    public synchronized void stop() {

        if (!running.get()) {
            return;
        }

        GlobalScreen.removeNativeKeyListener(this);

        this.eventConsumer = null;

        running.set(false);
    }

    @Override
    public boolean isRunning() {
        return running.get();
    }

    @Override
    public void nativeKeyPressed(
            NativeKeyEvent event
    ) {

        publish(
                new KeyboardEvent(
                        normalizeKey(event),
                        com.achhecode.browser_pilot.keyboardmonitor.domain.KeyboardEventType.PRESSED,
                        java.time.Instant.now()
                )
        );
    }

    @Override
    public void nativeKeyReleased(
            NativeKeyEvent event
    ) {

        publish(
                new KeyboardEvent(
                        normalizeKey(event),
                        com.achhecode.browser_pilot.keyboardmonitor.domain.KeyboardEventType.RELEASED,
                        java.time.Instant.now()
                )
        );
    }

    @Override
    public void nativeKeyTyped(
            NativeKeyEvent event
    ) {
        // Intentionally ignored.
    }

    private void publish(
            KeyboardEvent event
    ) {

        Consumer<KeyboardEvent> consumer =
                this.eventConsumer;

        if (consumer != null && running.get()) {
            consumer.accept(event);
        }
    }

    private String normalizeKey(
            NativeKeyEvent event
    ) {

        return switch (event.getKeyCode()) {

            case NativeKeyEvent.VC_UP ->
                    "ArrowUp";

            case NativeKeyEvent.VC_DOWN ->
                    "ArrowDown";

            case NativeKeyEvent.VC_LEFT ->
                    "ArrowLeft";

            case NativeKeyEvent.VC_RIGHT ->
                    "ArrowRight";

            default ->
                    NativeKeyEvent.getKeyText(
                            event.getKeyCode()
                    );
        };
    }
}