package com.ticketblock.exception;

/**
 * Exception thrown when attempting to resell a non-resellable ticket.
 * 
 * This exception is used when users try to resell tickets that were
 * purchased without paying the resale fee.
 */
public class NonResellableTicketException extends AppException {
    public NonResellableTicketException(String message, String userMessage) {
        super(message, userMessage);
    }

    public NonResellableTicketException(String message) {
        super(message);
    }
}
