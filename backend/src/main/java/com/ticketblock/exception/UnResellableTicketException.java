package com.ticketblock.exception;

public class UnResellableTicketException extends RuntimeException {
    public UnResellableTicketException(String message) {
        super(message);
    }
}
