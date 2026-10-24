package com.achhecode.linkedinservice;

import java.util.Arrays;
import java.util.Optional;

public enum LinkedInGame {

    CROSSCLIMB("/games/crossclimb/", GridLocation.EMPTY_LEFT),
    PATCHES("/games/patches/", GridLocation.EMPTY_LEFT),
    ZIP("/games/zip/", GridLocation.EMPTY_LEFT),
    MINI_SUDOKU("/games/mini-sudoku/", GridLocation.EMPTY_LEFT),
    WEND("/games/wend/", GridLocation.EMPTY_RIGHT),
    TANGO("/games/tango/", GridLocation.EMPTY_LEFT),
    QUEENS("/games/queens/", GridLocation.EMPTY_LEFT),
    PINPOINT("/games/pinpoint/", GridLocation.EMPTY_LEFT);

    private static final String BASE_URL = "https://www.linkedin.com";

    private final String path;
    private final GridLocation location;

    LinkedInGame(String path, GridLocation location) {
        this.path = path;
        this.location = location;
    }

    public String path() {
        return path;
    }

    public GridLocation location() {
        return location;
    }

    public String url() {
        return BASE_URL + path;
    }

    public static Optional<LinkedInGame> fromPath(String path) {
        return Arrays.stream(values())
                .filter(game -> game.path.equals(path))
                .findFirst();
    }
}
