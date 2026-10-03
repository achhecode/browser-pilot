package com.achhecode.browser_pilot.screen;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;

public interface CoreFoundation extends Library {

    CoreFoundation INSTANCE =
            Native.load("CoreFoundation", CoreFoundation.class);

    void CFRelease(Pointer ref);
}