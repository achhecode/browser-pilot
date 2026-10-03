package com.achhecode.browser_pilot.website.linkedin;

public record GridLocation(int x, int y) {

    public static final GridLocation EMPTY_LEFT = new GridLocation(350, 650);
    public static final GridLocation EMPTY_RIGHT = new GridLocation(1100, 650);
}