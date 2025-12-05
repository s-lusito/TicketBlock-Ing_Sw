package com.ticketblock.exception;

import lombok.Getter;

@Getter
public class InvalidDateAndTimeException extends AppException {
    public InvalidDateAndTimeException(String message, String userMessage) {
        super(message, userMessage);
    }

    public InvalidDateAndTimeException(String message) {
        super(message);
    }
}
