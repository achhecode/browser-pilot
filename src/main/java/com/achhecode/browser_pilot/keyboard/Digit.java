package com.achhecode.browser_pilot.keyboard;

import java.awt.event.KeyEvent;

public enum Digit {


    ZERO(0, '0', KeyEvent.VK_0),
    ONE(1, '1', KeyEvent.VK_1),
    TWO(2, '2', KeyEvent.VK_2),
    THREE(3, '3', KeyEvent.VK_3),
    FOUR(4, '4', KeyEvent.VK_4),
    FIVE(5, '5', KeyEvent.VK_5),
    SIX(6, '6', KeyEvent.VK_6),
    SEVEN(7, '7', KeyEvent.VK_7),
    EIGHT(8, '8', KeyEvent.VK_8),
    NINE(9, '9', KeyEvent.VK_9);

    private final int value;
    private final char character;
    private final int keyCode;

    Digit(int value, char character, int keyCode) {
        this.value = value;
        this.character = character;
        this.keyCode = keyCode;
    }

    public int getValue() {
        return value;
    }
    
    public char getCharacter() {
        return character;
    }

    public int getKeyCode() {
        return keyCode;
    }
}