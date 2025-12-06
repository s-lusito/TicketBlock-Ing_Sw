package com.ticketblock.exception;

/**
 * Exception thrown when a user attempts an action they are not authorized to perform.
 * 
 * This exception is used when users try to delete events they don't organize,
 * resell tickets they don't own, or exceed the maximum ticket purchase limit.
 */
public class ForbiddenActionException extends AppException {
    public ForbiddenActionException(String message, String userMessage) {
        super(message, userMessage);
    }

    public ForbiddenActionException(String message) {
        super(message);
    }
}
