package com.achhecode.browser_pilot.keyboard;

import java.awt.event.KeyEvent;

public enum MacKey {

    // =========================
    // Letters
    // =========================

    A(KeyEvent.VK_A),
    B(KeyEvent.VK_B),
    C(KeyEvent.VK_C),
    D(KeyEvent.VK_D),
    E(KeyEvent.VK_E),
    F(KeyEvent.VK_F),
    G(KeyEvent.VK_G),
    H(KeyEvent.VK_H),
    I(KeyEvent.VK_I),
    J(KeyEvent.VK_J),
    K(KeyEvent.VK_K),
    L(KeyEvent.VK_L),
    M(KeyEvent.VK_M),
    N(KeyEvent.VK_N),
    O(KeyEvent.VK_O),
    P(KeyEvent.VK_P),
    Q(KeyEvent.VK_Q),
    R(KeyEvent.VK_R),
    S(KeyEvent.VK_S),
    T(KeyEvent.VK_T),
    U(KeyEvent.VK_U),
    V(KeyEvent.VK_V),
    W(KeyEvent.VK_W),
    X(KeyEvent.VK_X),
    Y(KeyEvent.VK_Y),
    Z(KeyEvent.VK_Z),

    // =========================
    // Numbers
    // =========================

    NUM_0(KeyEvent.VK_0),
    NUM_1(KeyEvent.VK_1),
    NUM_2(KeyEvent.VK_2),
    NUM_3(KeyEvent.VK_3),
    NUM_4(KeyEvent.VK_4),
    NUM_5(KeyEvent.VK_5),
    NUM_6(KeyEvent.VK_6),
    NUM_7(KeyEvent.VK_7),
    NUM_8(KeyEvent.VK_8),
    NUM_9(KeyEvent.VK_9),

    // =========================
    // Function Keys
    // =========================

    F1(KeyEvent.VK_F1),
    F2(KeyEvent.VK_F2),
    F3(KeyEvent.VK_F3),
    F4(KeyEvent.VK_F4),
    F5(KeyEvent.VK_F5),
    F6(KeyEvent.VK_F6),
    F7(KeyEvent.VK_F7),
    F8(KeyEvent.VK_F8),
    F9(KeyEvent.VK_F9),
    F10(KeyEvent.VK_F10),
    F11(KeyEvent.VK_F11),
    F12(KeyEvent.VK_F12),
    F13(KeyEvent.VK_F13),
    F14(KeyEvent.VK_F14),
    F15(KeyEvent.VK_F15),
    F16(KeyEvent.VK_F16),
    F17(KeyEvent.VK_F17),
    F18(KeyEvent.VK_F18),
    F19(KeyEvent.VK_F19),
    F20(KeyEvent.VK_F20),

    // =========================
    // Arrow Keys
    // =========================

    LEFT(KeyEvent.VK_LEFT),
    RIGHT(KeyEvent.VK_RIGHT),
    UP(KeyEvent.VK_UP),
    DOWN(KeyEvent.VK_DOWN),

    // =========================
    // Editing / Navigation
    // =========================

    ENTER(KeyEvent.VK_ENTER),
    TAB(KeyEvent.VK_TAB),
    SPACE(KeyEvent.VK_SPACE),
    BACKSPACE(KeyEvent.VK_BACK_SPACE),
    DELETE(KeyEvent.VK_DELETE),
    ESCAPE(KeyEvent.VK_ESCAPE),

    HOME(KeyEvent.VK_HOME),
    END(KeyEvent.VK_END),
    PAGE_UP(KeyEvent.VK_PAGE_UP),
    PAGE_DOWN(KeyEvent.VK_PAGE_DOWN),

    INSERT(KeyEvent.VK_INSERT),

    // =========================
    // Modifiers
    // =========================

    SHIFT(KeyEvent.VK_SHIFT),
    CONTROL(KeyEvent.VK_CONTROL),
    ALT(KeyEvent.VK_ALT),
    META(KeyEvent.VK_META),

    // =========================
    // Punctuation / Symbols
    // =========================

    COMMA(KeyEvent.VK_COMMA),
    PERIOD(KeyEvent.VK_PERIOD),
    SLASH(KeyEvent.VK_SLASH),
    SEMICOLON(KeyEvent.VK_SEMICOLON),
    EQUALS(KeyEvent.VK_EQUALS),
    MINUS(KeyEvent.VK_MINUS),
    OPEN_BRACKET(KeyEvent.VK_OPEN_BRACKET),
    CLOSE_BRACKET(KeyEvent.VK_CLOSE_BRACKET),
    BACK_SLASH(KeyEvent.VK_BACK_SLASH),
    QUOTE(KeyEvent.VK_QUOTE),
    BACK_QUOTE(KeyEvent.VK_BACK_QUOTE),

    // =========================
    // Other
    // =========================

    CAPS_LOCK(KeyEvent.VK_CAPS_LOCK),
    NUM_LOCK(KeyEvent.VK_NUM_LOCK),
    SCROLL_LOCK(KeyEvent.VK_SCROLL_LOCK),
    PRINT_SCREEN(KeyEvent.VK_PRINTSCREEN),
    PAUSE(KeyEvent.VK_PAUSE),
    CONTEXT_MENU(KeyEvent.VK_CONTEXT_MENU);

    private final int keyCode;

    MacKey(int keyCode) {
        this.keyCode = keyCode;
    }

    public int getKeyCode() {
        return keyCode;
    }
}