package com.achhecode.browser_pilot.keyboard;

import java.util.HashMap;
import java.util.Map;

public final class MacKeys {

    private MacKeys() {}

    // Virtual key codes
    public static final short COMMAND = 55;
    public static final short SHIFT = 56; // Left Shift
    public static final short RIGHT_SHIFT = 60; // Right Shift
    public static final short RETURN = 36;
    public static final short TAB = 48;
    public static final short SPACE = 49;
    public static final short LEFT = 123;
    public static final short RIGHT = 124;
    public static final short DOWN = 125;
    public static final short UP = 126;
    public static final short V = 9;


    // Modifier flags
    public static final long FLAG_NONE = 0L;
    public static final long FLAG_SHIFT = 0x00020000L;
    public static final long FLAG_COMMAND = 0x00100000L;

    public record Stroke(short code, long flags) {}

    private static final Map<Character, Stroke> CHARS = new HashMap<>();

    static {
        // The character's index in this string IS its macOS (ANSI) key code.
        // '\0' marks codes that aren't printable keys (10 = ISO key, 36 = return, 48 = tab, 49 = space).
        String table = "asdfhgzxcv" + "\0" + "bqweryt" + "123465=97-80]" + "ou[ip"
                + "\0" + "lj'k;\\,/nm." + "\0\0" + "`";
        for (int code = 0; code < table.length(); code++) {
            char c = table.charAt(code);
            if (c == '\0') continue;
            CHARS.put(c, new Stroke((short) code, FLAG_NONE));
            if (Character.isLetter(c)) {
                CHARS.put(Character.toUpperCase(c), new Stroke((short) code, FLAG_SHIFT));
            }
        }

        // Shifted symbols -> same key as their unshifted twin, with Shift held
        String from = "!@#$%^&*()_+{}|:\"<>?~";
        String to   = "1234567890-=[]\\;',./`";
        for (int i = 0; i < from.length(); i++) {
            Stroke base = CHARS.get(to.charAt(i));
            CHARS.put(from.charAt(i), new Stroke(base.code(), FLAG_SHIFT));
        }

        CHARS.put(' ', new Stroke(SPACE, FLAG_NONE));
        CHARS.put('\t', new Stroke(TAB, FLAG_NONE));
        CHARS.put('\n', new Stroke(RETURN, FLAG_NONE));
    }

    /** Returns the key stroke for a character, or null if it isn't on the ANSI map. */
    public static Stroke forChar(char c) {
        return CHARS.get(c);
    }
}