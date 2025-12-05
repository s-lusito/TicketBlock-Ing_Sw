package com.ticketblock.exception;




public class ForbiddenActionException extends AppException {
    public ForbiddenActionException(String message, String userMessage) {
        super(message, userMessage);
    }

    public ForbiddenActionException(String message) {
        super(message);
    }
}
