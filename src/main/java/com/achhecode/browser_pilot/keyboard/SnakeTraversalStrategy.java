package com.achhecode.browser_pilot.keyboard;



public enum SnakeTraversalStrategy {

    LEFT_TO_RIGHT_SNAKE {
        @Override
        public boolean isLeftToRight(int row) {
            return row % 2 == 0;
        }
    },

    RIGHT_TO_LEFT_SNAKE {
        @Override
        public boolean isLeftToRight(int row) {
            return row % 2 != 0;
        }
    };

    //     ROW_MAJOR,
    //     COLUMN_MAJOR,
    //     SPIRAL

    public abstract boolean isLeftToRight(int row);
}