package com.achhecode.browser_pilot.keyboardmonitor.filter;

import com.achhecode.browser_pilot.keyboardmonitor.domain.KeyboardEvent;
import org.springframework.stereotype.Component;

@Component
public class AllKeyboardEventFilter
        implements KeyboardEventFilter {

    @Override
    public boolean supports(KeyboardEvent event) {
        return true;
    }
}