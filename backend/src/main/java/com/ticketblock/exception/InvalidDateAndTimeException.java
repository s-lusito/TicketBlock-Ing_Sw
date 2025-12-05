package com.ticketblock.exception;


public class InvalidDateAndTimeException extends AppException {
    public InvalidDateAndTimeException(String message, String userMessage) {
        super(message, userMessage);
    }

    public InvalidDateAndTimeException(String message) {
        super(message);
    }
}
