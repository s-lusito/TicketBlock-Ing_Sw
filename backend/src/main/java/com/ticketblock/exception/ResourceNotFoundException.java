package com.ticketblock.exception;

/**
 * Exception thrown when a requested resource cannot be found.
 * 
 * This exception is used when attempting to access entities (such as events,
 * tickets, venues, or users) that do not exist in the database.
 */
public class ResourceNotFoundException extends AppException {
    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, String userMessage) {
        super(message, userMessage);

    }

}
