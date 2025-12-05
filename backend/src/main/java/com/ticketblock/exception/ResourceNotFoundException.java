package com.ticketblock.exception;

public class ResourceNotFoundException extends AppException {
    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, String userMessage) {
        super(message, userMessage);

    }

}
