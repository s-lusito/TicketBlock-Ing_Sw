package com.ticketblock.exception;

public class VenueNotAvailableException extends AppException {
    public VenueNotAvailableException(String message, String userMessage) {
        super(message, userMessage);
    }

    public VenueNotAvailableException(String message) {
        super(message);
    }
}
