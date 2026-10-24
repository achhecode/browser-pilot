package com.achhecode.linkedinservice;

public record GridLocation(int x, int y) {

    public static final GridLocation EMPTY_LEFT = new GridLocation(350, 450);
    public static final GridLocation EMPTY_RIGHT = new GridLocation(1100, 650);
}