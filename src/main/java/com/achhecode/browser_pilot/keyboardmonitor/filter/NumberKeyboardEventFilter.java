package com.achhecode.browser_pilot.keyboardmonitor.filter;

import com.achhecode.browser_pilot.keyboardmonitor.domain.KeyboardEvent;
import org.springframework.stereotype.Component;

@Component
public class NumberKeyboardEventFilter
        implements KeyboardEventFilter {

    @Override
    public boolean supports(KeyboardEvent event) {

        String key = event.key();

        return key != null
                && key.length() == 1
                && Character.isDigit(key.charAt(0));
    }
}