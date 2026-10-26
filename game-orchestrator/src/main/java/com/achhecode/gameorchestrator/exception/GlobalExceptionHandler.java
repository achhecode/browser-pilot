
package com.achhecode.gameorchestrator.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleBadRequest(IllegalArgumentException ex) {

        return ResponseEntity.badRequest()
                .body(Map.of("status", "FAILED", "stage", "VALIDATION", "message", ex.getMessage()));
    }

    @ExceptionHandler(GameOrchestrationException.class)
    public ResponseEntity<Map<String, Object>> handleOrchestrationFailure(GameOrchestrationException ex) {

        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(Map.of("status", "FAILED", "stage", ex.getStage(), "message", ex.getMessage()));
    }
}
