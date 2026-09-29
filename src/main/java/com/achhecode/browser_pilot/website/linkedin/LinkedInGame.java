package com.achhecode.browser_pilot.website.linkedin;

public enum LinkedInGame {

    CROSSCLIMB("/games/crossclimb/"),
    PATCHES("/games/patches/"),
    ZIP("/games/zip/"),
    MINI_SUDOKU("/games/mini-sudoku/"),
    WEND("/games/wend/"),
    TANGO("/games/tango/"),
    QUEENS("/games/queens/");

    private final String path;

    LinkedInGame(String path) {
        this.path = path;
    }

    public String path() {
        return path;
    }
}