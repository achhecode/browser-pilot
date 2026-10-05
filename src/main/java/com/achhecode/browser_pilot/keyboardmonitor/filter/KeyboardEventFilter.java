package com.achhecode.browser_pilot.keyboardmonitor.filter;

import com.achhecode.browser_pilot.keyboardmonitor.domain.KeyboardEvent;

public interface KeyboardEventFilter {

    boolean supports(KeyboardEvent event);
}