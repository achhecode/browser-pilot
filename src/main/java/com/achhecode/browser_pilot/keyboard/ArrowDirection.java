package com.achhecode.browser_pilot.keyboard;

import java.awt.event.KeyEvent;

public enum ArrowDirection {

    LEFT(KeyEvent.VK_LEFT),
    RIGHT(KeyEvent.VK_RIGHT),
    UP(KeyEvent.VK_UP),
    DOWN(KeyEvent.VK_DOWN);

    private final int keyCode;

    ArrowDirection(int keyCode) {
        this.keyCode = keyCode;
    }

    public int getKeyCode() {
        return keyCode;
    }
}