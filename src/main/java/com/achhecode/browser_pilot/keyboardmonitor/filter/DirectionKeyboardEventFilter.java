package com.achhecode.browser_pilot.keyboardmonitor.filter;

import com.achhecode.browser_pilot.keyboardmonitor.domain.Direction;
import com.achhecode.browser_pilot.keyboardmonitor.domain.KeyboardEvent;
import org.springframework.stereotype.Component;

@Component
public class DirectionKeyboardEventFilter
        implements KeyboardEventFilter {

    @Override
    public boolean supports(KeyboardEvent event) {

        return Direction
                .fromKey(event.key())
                .isPresent();
    }
}