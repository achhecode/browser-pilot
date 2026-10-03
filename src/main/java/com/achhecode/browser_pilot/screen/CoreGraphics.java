package com.achhecode.browser_pilot.screen;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;

import java.util.Arrays;
import java.util.List;

public interface CoreGraphics extends Library {

    CoreGraphics INSTANCE = Native.load("CoreGraphics", CoreGraphics.class);

    Pointer CGEventCreateMouseEvent(
            Pointer source,
            int mouseType,
            CGPoint point,
            int mouseButton
    );

    void CGEventPost(int tap, Pointer event);

    class CGPoint extends Structure implements Structure.ByValue {

        public double x;
        public double y;

        public CGPoint(double x, double y) {
            this.x = x;
            this.y = y;
        }

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("x", "y");
        }
    }
}