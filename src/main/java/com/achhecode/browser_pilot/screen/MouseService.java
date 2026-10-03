package com.achhecode.browser_pilot.screen;

import com.sun.jna.Pointer;
import org.springframework.stereotype.Service;

@Service
public class MouseService {

    private static final CoreGraphics CG = CoreGraphics.INSTANCE;
    private static final CoreFoundation CF = CoreFoundation.INSTANCE;

    public void click(int x, int y) {
        CoreGraphics.CGPoint point =
                new CoreGraphics.CGPoint(x, y);

        postMouseEvent(1, point); // mouse down
        postMouseEvent(2, point); // mouse up
    }

    private void postMouseEvent(
            int type,
            CoreGraphics.CGPoint point) {

        Pointer event = CG.CGEventCreateMouseEvent(
                null,
                type,
                point,
                0
        );

        if (event == null) {
            throw new IllegalStateException(
                    "Could not create mouse event"
            );
        }

        try {
            CG.CGEventPost(0, event);
        } finally {
            CF.CFRelease(event);
        }
    }
}