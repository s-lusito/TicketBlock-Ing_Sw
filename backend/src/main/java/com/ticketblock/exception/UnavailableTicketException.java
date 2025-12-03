package com.ticketblock.exception;

public class UnavailableTicketException extends RuntimeException {
    public UnavailableTicketException(String message) {
        super(message);
    }
}
