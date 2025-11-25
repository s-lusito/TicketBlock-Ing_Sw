package com.ticketblock.controller;

import com.ticketblock.dto.Response.ApiFieldError;
import com.ticketblock.dto.Response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.jsonwebtoken.ExpiredJwtException;

import java.util.ArrayList;
import java.util.List;

/**
 * Global exception handler for handling exceptions across the Spring Boot application.
 *
 * This class is annotated with {@code @RestControllerAdvice} which combines the functionalities
 * of {@code @ControllerAdvice} and {@code @ResponseBody}. It listens for exceptions thrown
 * from any controller and ensures that error details are serialized into a JSON response.
 *
 */
@RestControllerAdvice //Response body + controller advice
// @ControllerAdvice: questa classe ascolta tutte le eccezioni lanciate da qualsiasi controller
// @Response Body: annotazione che si mette nei metodi affinchè i valori restituiti dai metodi vengono serializzati in JSON
public class GlobalExceptionHandler {

    //cattura in automatico questo tipo di eccezioni lanciate, senza bisogno del try catch
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<?> handleUsernameNotFoundException(UsernameNotFoundException exception) {
        ErrorResponse errorResponse= ErrorResponse
                .builder()
                .message(exception.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    // Gestisci token scaduto
    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<?> handleExpiredJwtException(ExpiredJwtException exception) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message("Token expired")
                .status(status.value())
                .build();
        return ResponseEntity.status(status).body(errorResponse);
    }

    // Gestisci accesso negato (403)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDeniedException(AccessDeniedException exception) {
        HttpStatus status = HttpStatus.FORBIDDEN;
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message("Access denied")
                .status(status.value())
                .build();
        return ResponseEntity.status(status).body(errorResponse);
    }

    // Gestisci anche eccezioni generiche
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGenericException(Exception exception) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ErrorResponse errorResponse= ErrorResponse.builder()
                .message("Internal Server Error")
                .status(status.value())
                .build();
        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException exception) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ErrorResponse errorResponse= ErrorResponse.builder()
                .message(exception.getMessage())
                .status(status.value())
                .build();
        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        List<ApiFieldError> fieldErrors = new ArrayList<>();
        for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
            fieldErrors.add(
                    ApiFieldError.builder()
                            .field(fieldError.getField())
                            .message(fieldError.getDefaultMessage())
                            .build()
            );
        }
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ErrorResponse errorResponse = ErrorResponse.builder().message("Validation Failed").status(status.value()).errors(fieldErrors).build();
        return ResponseEntity.status(status).body(errorResponse);


    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleBadCredentialsException(BadCredentialsException exception) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        ErrorResponse errorResponse= ErrorResponse.builder()
                .message("Authentication failed: Invalid email or password")
                .status(status.value())
                .build();
        return ResponseEntity.status(status).body(errorResponse);
    }



}
