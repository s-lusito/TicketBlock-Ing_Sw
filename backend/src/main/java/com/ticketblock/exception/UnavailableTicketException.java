package com.ticketblock.exception;

public class UnavailableTicketException extends AppException {

    public UnavailableTicketException(String message) {
        super(message);
    }

    public UnavailableTicketException(String message, String userMessage) {
        super(message,  userMessage);


    }
}
