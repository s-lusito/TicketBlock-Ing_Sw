package com.ticketblock.exception;

public class VenueNotAvailableException extends RuntimeException {
    public VenueNotAvailableException(String message) {
        super(message);
    }
}
