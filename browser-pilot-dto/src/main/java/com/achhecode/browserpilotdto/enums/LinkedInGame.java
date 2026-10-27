package com.achhecode.browserpilotdto.enums;

import java.util.Arrays;
import java.util.Optional;

import com.achhecode.browserpilotdto.dto.GridLocation;

public enum LinkedInGame {

    CROSSCLIMB("crossclimb", "/games/crossclimb/", GridLocation.EMPTY_LEFT),
    PATCHES("patches", "/games/patches/", GridLocation.EMPTY_LEFT), 
    ZIP("zip", "/games/zip/", GridLocation.EMPTY_LEFT),
    MINI_SUDOKU("mini-sudoku", "/games/mini-sudoku/", GridLocation.EMPTY_LEFT),
    WEND("wend", "/games/wend/", GridLocation.EMPTY_RIGHT), 
    TANGO("tango", "/games/tango/", GridLocation.EMPTY_LEFT),
    QUEENS("queens", "/games/queens/", GridLocation.EMPTY_LEFT),
    PINPOINT("pinpoint", "/games/pinpoint/", GridLocation.EMPTY_LEFT);

    private static final String BASE_URL = "https://www.linkedin.com";

    private final String keyName;
    private final String path;
    private final GridLocation location;

    LinkedInGame(String keyName, String path, GridLocation location) {
        this.keyName = keyName;
        this.path = path;
        this.location = location;
    }

    public String keyName() {
        return keyName;
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
        return Arrays.stream(values()).filter(game -> game.path.equals(path)).findFirst();
    }
}
