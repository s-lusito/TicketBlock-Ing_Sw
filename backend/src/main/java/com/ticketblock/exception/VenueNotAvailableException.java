package com.ticketblock.exception;

/**
 * Exception thrown when a venue is not available for an event.
 * 
 * This exception is used when attempting to schedule an event at a venue
 * that is already booked for overlapping date and time slots.
 */
public class VenueNotAvailableException extends AppException {
    public VenueNotAvailableException(String message, String userMessage) {
        super(message, userMessage);
    }

    public VenueNotAvailableException(String message) {
        super(message);
    }
}
