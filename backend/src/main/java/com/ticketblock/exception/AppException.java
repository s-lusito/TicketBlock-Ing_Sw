package com.ticketblock.exception;

import lombok.Getter;

/**
 * Base exception class for all application-specific exceptions.
 * 
 * This exception provides dual messaging capability: one message for developers
 * and one user-friendly message for end users. All custom exceptions in the
 * application should extend this class.
 */
@Getter
public class AppException extends RuntimeException {
    private String userMessage;

    public AppException(String message, String userMessage) {
        super(message);
        this.userMessage = userMessage;
    }

    public AppException(String message) { // setta lo stesso messaggio per dev e user
        super(message);
        this.userMessage = message;
    }


}
