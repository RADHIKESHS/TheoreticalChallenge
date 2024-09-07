package com.prospecta.Theoretical_Challenge.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler for the application.
 * Catches specific and general exceptions and returns appropriate responses.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle CsvProcessingException and return a BAD_REQUEST response.
     */
    @ExceptionHandler(CsvProcessingException.class)
    public ResponseEntity<String> handleCsvProcessingException(CsvProcessingException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Handle general exceptions and return an INTERNAL_SERVER_ERROR response.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception ex) {
        return new ResponseEntity<>("An error occurred: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

