package com.ticketblock.exception;

public class NonResellableTicketException extends AppException {
    public NonResellableTicketException(String message, String userMessage) {
        super(message, userMessage);
    }

    public NonResellableTicketException(String message) {
        super(message);
    }
}
