
package com.achhecode.gameorchestrator.exception;

public class GameOrchestrationException extends RuntimeException {

    private final String stage;

    public GameOrchestrationException(String stage, String message) {
        super(message);
        this.stage = stage;
    }

    public GameOrchestrationException(String stage, String message, Throwable cause) {
        super(message, cause);
        this.stage = stage;
    }

    public String getStage() {
        return stage;
    }
}
