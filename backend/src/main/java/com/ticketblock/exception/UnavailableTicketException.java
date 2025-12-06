package com.ticketblock.exception;

/**
 * Exception thrown when attempting to purchase unavailable tickets.
 * 
 * This exception is used when tickets have already been sold, when tickets
 * are not yet on sale, or when the event sales have ended.
 */
public class UnavailableTicketException extends AppException {

    public UnavailableTicketException(String message) {
        super(message);
    }

    public UnavailableTicketException(String message, String userMessage) {
        super(message,  userMessage);


    }
}
