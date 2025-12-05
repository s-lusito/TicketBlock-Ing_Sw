package com.ticketblock.exception;

public class UnResellableTicketException extends AppException {
    public UnResellableTicketException(String message, String userMessage) {
        super(message, userMessage);
    }

    public UnResellableTicketException(String message) {
        super(message);
    }
}
