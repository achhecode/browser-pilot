package com.achhecode.browser_pilot.website.linkedin.page.games.tango;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TangoCommandGenerator {

    private final TangoCommandParser parser;

    public TangoCommandGenerator(TangoCommandParser parser) {
        this.parser = parser;
    }

    public List<TangoCommand> generate(String instruction) {
        return parser.parse(instruction);
    }
}