package org.hiero.spring.sample.web;

import java.util.Map;
import org.hiero.base.HieroException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Global exception handler for the sample application. centralizes error handling for a cleaner
 * architectural design.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(HieroException.class)
  public ResponseEntity<Map<String, String>> handleHieroException(final HieroException ex) {
    ex.printStackTrace();
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(Map.of("error", "Hiero Network Error", "message", ex.getMessage()));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<?> handleGeneralException(Exception ex) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(Map.of("error", "System Error", "message", ex.getMessage()));
  }
}
