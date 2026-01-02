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
 * Global exception handler for handling exceptions across the Spring Boot
 * application.

 *
 */
@RestControllerAdvice // Response body + controller advice
// @ControllerAdvice: questa classe ascolta tutte le eccezioni lanciate da
// qualsiasi controller
// @Response Body: annotazione che si mette nei metodi affinchè i valori
// restituiti dai metodi vengono serializzati in JSON
@Slf4j // per i log

// TODO in futuro si può passare ad usare ProblemDetail di spring invece di ErrorResponse personalizzato
public class GlobalExceptionHandler {

    // NOT FOUND 404

    //cattura in automatico questo tipo di eccezioni lanciate, senza bisogno del try catch
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<?> handleUsernameNotFoundException(UsernameNotFoundException exception) {
        log.warn(exception.getMessage(), exception);
        HttpStatus status = HttpStatus.NOT_FOUND;
        ErrorResponse errorResponse = ErrorResponse
                .builder()
                .detail("User not found")
                .userMessage("User not found")
                .status(status.value())
                .build();
        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFoundException(ResourceNotFoundException exception) {
        log.warn(exception.getMessage(), exception);
        return buildResponseEntity(exception, HttpStatus.NOT_FOUND);


    }


    // UNAUTHORIZED 401

    // Gestisci token scaduto
    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<?> handleExpiredJwtException(ExpiredJwtException exception) {
        log.warn(exception.getMessage(), exception);
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        ErrorResponse errorResponse = ErrorResponse.builder()
                .detail("JWT token has expired")
                .userMessage("Session has expired, please log in again")
                .status(status.value())
                .build();
        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleBadCredentialsException(BadCredentialsException exception) {
        log.error(exception.getMessage(), exception);
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        ErrorResponse errorResponse = ErrorResponse.builder()
                .userMessage("Authentication failed: Invalid email or password")
                .detail("Authentication failed: Invalid email or password")
                .status(status.value())
                .build();
        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(ForbiddenActionException.class)
    public ResponseEntity<?> handleForbiddenActionException(ForbiddenActionException exception) {
        log.warn(exception.getMessage(), exception);
        return buildResponseEntity(exception, HttpStatus.FORBIDDEN);

    }

    // FORBIDDEN 403 ( Accesso negato: Utente autenticato ma non autorizzato a fare l’azione)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDeniedException(AccessDeniedException exception) {
        log.warn(exception.getMessage(), exception);
        HttpStatus status = HttpStatus.FORBIDDEN;
        ErrorResponse errorResponse = ErrorResponse.builder()
                .detail("Access denied")
                .userMessage("Access denied")
                .status(status.value())
                .build();
        return ResponseEntity.status(status).body(errorResponse);
    }

    // INTERNAL SERVER ERROR 500
    // Gestisci anche eccezioni generiche
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGenericException(Exception exception) {
        log.error(exception.getMessage(), exception);
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ErrorResponse errorResponse = ErrorResponse.builder()
                .detail("Internal Server Error")
                .userMessage("Internal Server Error")
                .status(status.value())
                .build();
        return ResponseEntity.status(status).body(errorResponse);
    }

    // BAD REQUEST 400
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException exception) {
        log.warn(exception.getMessage(), exception);
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ErrorResponse errorResponse = ErrorResponse.builder()
                .detail("Invalid Request")
                .userMessage("Invalid Request")
                .status(status.value())
                .build();
        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        log.warn(exception.getMessage(), exception);
        HttpStatus status = HttpStatus.BAD_REQUEST;
        List<ApiFieldError> fieldErrors = new ArrayList<>();
        for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
            fieldErrors.add(
                    ApiFieldError.builder()
                            .field(fieldError.getField())
                            .message(fieldError.getDefaultMessage())
                            .build());
        }
        ErrorResponse errorResponse = ErrorResponse.builder()
                .detail("Validation Failed").userMessage("Provided data are nor valid:")
                .status(status.value())
                .errors(fieldErrors)
                .build();
        return ResponseEntity.status(status).body(errorResponse);


    }

    @ExceptionHandler(InvalidRoleException.class)
    public ResponseEntity<?> handleInvalidRoleException(InvalidRoleException exception) {
        log.warn(exception.getMessage(), exception);
        return buildResponseEntity(exception, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException exception) {
        log.warn(exception.getMessage(), exception);
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ErrorResponse errorResponse = ErrorResponse.builder()
                .detail(exception.getMostSpecificCause().getMessage()) //TODO specificare di più
                .userMessage("Invalid request format")
                .status(status.value())
                .build();
        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(InvalidDateAndTimeException.class)
    public ResponseEntity<?> handleInvalidDateAndTimeException(InvalidDateAndTimeException exception) {
        log.warn(exception.getMessage(), exception);
        return buildResponseEntity(exception, HttpStatus.BAD_REQUEST);


    }


    // CONFLICT 409

    @ExceptionHandler({ VenueNotAvailableException.class, UnavailableTicketException.class,
            NonResellableTicketException.class, FailedPaymentException.class })
    public ResponseEntity<?> handleConflictException(AppException exception) {
        if (exception instanceof FailedPaymentException) {
            log.warn("Payment failed"); // nascondo dal log i dettagli dell'eccezione di pagamento
        } else {
            log.warn(exception.getMessage(), exception);
        }
        return buildResponseEntity(exception, HttpStatus.CONFLICT);
    }


    // TODO aggiungi Blockchain Exception
    private ResponseEntity<?> buildResponseEntity(AppException exception, HttpStatus status) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .detail(exception.getMessage())
                .userMessage(exception.getUserMessage())
                .status(status.value())
                .build();
        return ResponseEntity.status(status).body(errorResponse);
    }

}
