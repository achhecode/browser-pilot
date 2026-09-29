package com.achhecode.browser_pilot.keyboard;

public interface KeyboardService {

    void press(int keyCode);
    void switchTab();                 // Command + Tab
    void shiftTab();                 // Shift + Tab
    void pressEnter();
    void addDelay(long ms);

    void pressTab(int n);
    default void pressTab() { pressTab(1); }

    void pressSpace(int n);
    default void pressSpace() { pressSpace(1); }

    void pressLeft(int n);
    default void pressLeft() { pressLeft(1); }

    void pressRight(int n);
    default void pressRight() { pressRight(1); }

    void pressUp(int n);
    default void pressUp() { pressUp(1); }

    void pressDown(int n);
    default void pressDown() { pressDown(1); }

    void typeLetter(char c);
    void typeText(String text);       // key by key, in order
    void typeTextFast(String text);   // clipboard + Command+V

    void holdSpace();
    void releaseSpace();
}