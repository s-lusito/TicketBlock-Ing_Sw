package com.ticketblock.controller;

import com.ticketblock.dto.Response.ApiFieldError;
import com.ticketblock.dto.Response.ErrorResponse;
import com.ticketblock.exception.*;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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
@Slf4j //per i log
public class GlobalExceptionHandler {

    //cattura in automatico questo tipo di eccezioni lanciate, senza bisogno del try catch
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<?> handleUsernameNotFoundException(UsernameNotFoundException exception) {
        log.warn(exception.getMessage(), exception);
        HttpStatus status = HttpStatus.NOT_FOUND;
        ErrorResponse errorResponse= ErrorResponse
                .builder()
                .message("User not found")
                .status(status.value())
                .build();
        return ResponseEntity.status(status).body(errorResponse);
    }

    // Gestisci token scaduto
    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<?> handleExpiredJwtException(ExpiredJwtException exception) {
        log.warn(exception.getMessage(), exception);
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
        log.warn(exception.getMessage(), exception);
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
        log.error(exception.getMessage(), exception);
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ErrorResponse errorResponse= ErrorResponse.builder()
                .message("Internal Server Error")
                .status(status.value())
                .build();
        return ResponseEntity.status(status).body(errorResponse);
    }



    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException exception) {
        log.warn(exception.getMessage(), exception);
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ErrorResponse errorResponse= ErrorResponse.builder()
                .message("Invalid Request")
                .status(status.value())
                .build();
        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        log.warn(exception.getMessage(), exception);
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
        log.error(exception.getMessage(), exception);
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        ErrorResponse errorResponse= ErrorResponse.builder()
                .message("Authentication failed: Invalid email or password")
                .status(status.value())
                .build();
        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(InvalidRoleException.class)
    public ResponseEntity<?> handleInvalidRoleException(InvalidRoleException exception) {
        log.warn(exception.getMessage(), exception);
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(exception.getMessage())
                .status(status.value())
                .build();
        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(VenueNotAvailableException.class)
    public ResponseEntity<?> handleVenueNotAvailableException(VenueNotAvailableException exception) {
        log.warn(exception.getMessage(), exception);
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(exception.getMessage())
                .status(status.value())
                .build();
        return ResponseEntity.status(status).body(errorResponse);
    }


    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFoundException(ResourceNotFoundException exception) {
        log.warn(exception.getMessage(), exception);
        HttpStatus status = HttpStatus.NOT_FOUND;
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(exception.getMessage())
                .status(status.value())
                .build();
        return ResponseEntity.status(status).body(errorResponse);

    }


    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException exception) {
        log.warn(exception.getMessage(), exception);
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(exception.getMostSpecificCause().getMessage()) //TODO specificare di più
                .status(status.value())
                .build();
        return ResponseEntity.status(status).body(errorResponse);
    }
    @ExceptionHandler(UnauthorizedActionException.class)
    public ResponseEntity<?> handleUnauthorizedActionException(UnauthorizedActionException exception) {
        log.warn(exception.getMessage(), exception);
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(exception.getMessage())
                .status(status.value())
                .build();
        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(UnavailableTicketException.class)
    public ResponseEntity<?> handleUnavailableTicketException(UnavailableTicketException exception) {
        log.warn(exception.getMessage(), exception);
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(exception.getMessage())
                .status(status.value())
                .build();
        return ResponseEntity.status(status).body(errorResponse);
    }



}
