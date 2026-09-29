package com.achhecode.browser_pilot.keyboard;


import com.achhecode.browser_pilot.keyboard.MacKeys.Stroke;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import org.springframework.stereotype.Service;

import static com.achhecode.browser_pilot.keyboard.MacKeys.*;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

@Service
public class JnaKeyboardService implements KeyboardService {

    public interface CoreGraphics extends Library {
        CoreGraphics INSTANCE = Native.load("CoreGraphics", CoreGraphics.class);

        Pointer CGEventCreateKeyboardEvent(Pointer source, short virtualKey, boolean keyDown);
        void CGEventSetFlags(Pointer event, long flags);
        void CGEventPost(int tap, Pointer event);
        void CGEventKeyboardSetUnicodeString(Pointer event, long length, char[] string);
    }

    public interface CoreFoundation extends Library {
        CoreFoundation INSTANCE = Native.load("CoreFoundation", CoreFoundation.class);

        void CFRelease(Pointer ref);
    }

    private static final CoreGraphics CG = CoreGraphics.INSTANCE;
    private static final CoreFoundation CF = CoreFoundation.INSTANCE;

    @Override
    public void press(int keyCode) {

        switch (keyCode) {
            case KeyEvent.VK_LEFT -> tap(LEFT, FLAG_NONE);
            case KeyEvent.VK_RIGHT -> tap(RIGHT, FLAG_NONE);
            case KeyEvent.VK_UP -> tap(UP, FLAG_NONE);
            case KeyEvent.VK_DOWN -> tap(DOWN, FLAG_NONE);
            case KeyEvent.VK_ENTER -> tap(RETURN, FLAG_NONE);
            case KeyEvent.VK_TAB -> tap(TAB, FLAG_NONE);
            case KeyEvent.VK_SPACE -> tap(SPACE, FLAG_NONE);

            default -> throw new IllegalArgumentException(
                    "Unsupported key: " + keyCode
            );
        }
    }

    @Override
    public void switchTab() {
        post(COMMAND, true, FLAG_COMMAND);
        post(TAB, true, FLAG_COMMAND);
        post(TAB, false, FLAG_COMMAND);
        post(COMMAND, false, FLAG_NONE);
    }

    @Override public void pressEnter()      { tap(RETURN, FLAG_NONE); }
    @Override public void pressTab(int n)   { repeat(TAB, n); }
    @Override public void pressSpace(int n) { repeat(SPACE, n); }
    @Override public void pressLeft(int n)  { repeat(LEFT, n); }
    @Override public void pressRight(int n) { repeat(RIGHT, n); }
    @Override public void pressUp(int n)    { repeat(UP, n); }
    @Override public void pressDown(int n)  { repeat(DOWN, n); }

    @Override
    public void addDelay(long ms) {
        if (ms <= 0) return;
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void typeLetter(char c) {
        Stroke s = MacKeys.forChar(c);
        if (s != null) tap(s.code(), s.flags());
        else typeUnicode(c);
    }

    @Override
    public void typeText(String text) {
        if (text == null) return;
        for (int i = 0; i < text.length(); i++) typeLetter(text.charAt(i));
    }

    @Override
    public void typeTextFast(String text) {
        if (text == null || text.isEmpty()) return;
        setClipboard(text);
        addDelay(50);
        tap(V, FLAG_COMMAND);
        addDelay(50);
    }

    @Override public void holdSpace()    { 
        post(SPACE, true, FLAG_NONE); 
    }
    
    @Override public void releaseSpace() {
         post(SPACE, false, FLAG_NONE); 
    }

    // ---- Internals -------------------------------------------------------

    private void repeat(short keyCode, int n) {
        if (n < 1) throw new IllegalArgumentException("n must be >= 1");
        for (int i = 0; i < n; i++) tap(keyCode, FLAG_NONE);
    }

    private void tap(short keyCode, long flags) {
        post(keyCode, true, flags);
        post(keyCode, false, flags);
    }

    private void post(short keyCode, boolean down, long flags) {
        Pointer event = CG.CGEventCreateKeyboardEvent(null, keyCode, down);
        if (event == null) throw new IllegalStateException(
                "Could not create key event. Grant Accessibility permission to your terminal/IDE.");
        try {
            CG.CGEventSetFlags(event, flags);
            CG.CGEventPost(0, event); // 0 = kCGHIDEventTap
        } finally {
            CF.CFRelease(event);
        }
        addDelay(2); // tiny gap so events are never reordered or dropped
    }

    private void typeUnicode(char c) {
        char[] buf = {c};
        for (boolean down : new boolean[]{true, false}) {
            Pointer event = CG.CGEventCreateKeyboardEvent(null, (short) 0, down);
            try {
                CG.CGEventKeyboardSetUnicodeString(event, 1, buf);
                CG.CGEventPost(0, event);
            } finally {
                CF.CFRelease(event);
            }
            addDelay(2);
        }
    }

    private void setClipboard(String text) {
        try {
            Process p = new ProcessBuilder("pbcopy").start();
            try (OutputStream os = p.getOutputStream()) {
                os.write(text.getBytes(StandardCharsets.UTF_8));
            }
            p.waitFor();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to set clipboard", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}